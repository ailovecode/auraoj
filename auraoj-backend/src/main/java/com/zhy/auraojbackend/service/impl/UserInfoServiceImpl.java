package com.zhy.auraojbackend.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhy.auraojbackend.common.ErrorCode;
import com.zhy.auraojbackend.exception.BusinessException;
import com.zhy.auraojbackend.mapper.UserInfoMapper;
import com.zhy.auraojbackend.model.dto.PageRequest;
import com.zhy.auraojbackend.model.dto.PageResponse;
import com.zhy.auraojbackend.model.dto.user.request.UserLoginRequest;
import com.zhy.auraojbackend.model.dto.user.response.UserLoginResponse;
import com.zhy.auraojbackend.model.dto.user.request.UserRegisterRequest;
import com.zhy.auraojbackend.model.dto.user.request.UserUpdateRequest;
import com.zhy.auraojbackend.model.entity.UserInfo;
import com.zhy.auraojbackend.model.enums.UserRoleEnum;
import com.zhy.auraojbackend.model.vo.UserInfoVO;
import com.zhy.auraojbackend.service.MinioService;
import com.zhy.auraojbackend.service.UserInfoService;
import com.zhy.auraojbackend.utils.PasswordEncoderUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
* @author zhy
* @description 针对表【user_info(用户信息表)】的数据库操作Service实现
* @createDate 2026-02-08 17:00:45
*/
@Slf4j
@Service
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo>
    implements UserInfoService{
    private final ConcurrentMap<String, Object> userLocks = new ConcurrentHashMap<>();

    @Resource
    private MinioService minioService;

    @Override
    public Long userRegister(UserRegisterRequest userRegisterRequest) {
        // 参数校验
        if (userRegisterRequest == null) {
            throw new BusinessException(ErrorCode.BAD_PARAMS);
        }

        // 调用请求体自身的校验方法
        userRegisterRequest.check();

        String username = userRegisterRequest.getUsername();
        String phone = userRegisterRequest.getPhone();
        String email = userRegisterRequest.getEmail();

        Object lock = userLocks.computeIfAbsent(username, k -> new Object());
        /**
         * todo:
         * 限制用户名更改的时间一个月一次
         * 如果是被删除的用户，重新注册是恢复还是重新注册
         */
        synchronized (lock) {
            // 校验用户名是否重复
            LambdaQueryWrapper<UserInfo> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(UserInfo::getUsername, username);
            long count = this.count(queryWrapper);
            if (count > 0) {
                throw new BusinessException(ErrorCode.BAD_PARAMS, "用户名已存在");
            }

            // 插入数据
            UserInfo userInfo = new UserInfo();
            // 校验手机号是否重复
            if (StringUtils.isNotBlank(phone)) {
                queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(UserInfo::getPhone, phone);
                count = this.count(queryWrapper);
                if (count > 0) {
                    throw new BusinessException(ErrorCode.BAD_PARAMS, "手机号已被注册");
                }
                userInfo.setPhone(phone);
            }

            // 校验邮箱是否重复
            if (StringUtils.isNotBlank(email)) {
                queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(UserInfo::getEmail, email);
                count = this.count(queryWrapper);
                if (count > 0) {
                    throw new BusinessException(ErrorCode.BAD_PARAMS, "邮箱已被注册");
                }
                userInfo.setEmail(email);
            }

            // 加密密码
            String encryptPassword = PasswordEncoderUtil.encode(userRegisterRequest.getPassword());
            userInfo.setUsername(username);
            userInfo.setPassword(encryptPassword);
            userInfo.setGender(userRegisterRequest.getGender());
            userInfo.setSchool(userRegisterRequest.getSchool());
            // 判断是否为系统第一个用户，如果是则设置为管理员权限
            long userCount = this.count();
            
            if (userCount == 0) {
                // 第一个用户，设置为管理员
                userInfo.setRole(UserRoleEnum.ADMIN);
                log.info("检测到第一个用户注册，授予管理员权限: {}", username);
            }

            boolean saveResult = this.save(userInfo);
            if (!saveResult) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "注册失败");
            }

            return userInfo.getId();
        }
    }

    @Override
    public UserLoginResponse userLogin(UserLoginRequest userLoginRequest) {
        // 参数校验
        if (userLoginRequest == null) {
            throw new BusinessException(ErrorCode.BAD_PARAMS);
        }

        // 调用请求体自身的校验方法
        userLoginRequest.check();

        String username = userLoginRequest.getUsername();
        String password = userLoginRequest.getPassword();

        // 根据用户名或邮箱查询用户
        LambdaQueryWrapper<UserInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.and(wrapper -> wrapper.eq(UserInfo::getUsername, username)
                .or()
                .eq(UserInfo::getPhone, username)
                .or()
                .eq(UserInfo::getEmail, username));
        UserInfo userInfo = this.getOne(queryWrapper);

        // 校验用户是否存在
        if (userInfo == null) {
            throw new BusinessException(ErrorCode.BAD_PARAMS, "用户不存在");
        }

        // 校验账号状态
        if (userInfo.getStatus() != 0) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED, "账号不存在或者已被封禁，请联系管理员或重新注册");
        }

        // 校验密码
        if (!PasswordEncoderUtil.matches(password, userInfo.getPassword())) {
            throw new BusinessException(ErrorCode.BAD_PARAMS, "密码错误");
        }

        // 更新最后登录时间
        this.updateById(userInfo);

        // 使用Sa-Token进行登录
        StpUtil.login(userInfo.getId());

        // 构造返回信息
        UserLoginResponse loginResponse = new UserLoginResponse();
        BeanUtils.copyProperties(userInfo, loginResponse);
        loginResponse.setLoginTime(new Date());
        loginResponse.setToken(StpUtil.getTokenValue());
        String avatar = userInfo.getAvatar();
        if (StringUtils.isNotBlank(avatar)) {
            loginResponse.setAvatar(minioService.getFileUrl(avatar));
        }

        return loginResponse;
    }

    @Override
    public boolean userLogout() {
        // 检查是否已登录
        if (!StpUtil.isLogin()) {
            throw new BusinessException(ErrorCode.NO_LOGIN);
        }

        // 登出
        StpUtil.logout();
        return true;
    }

    @Override
    public UserInfoVO getCurrentUser() {
        // 检查是否已登录
        if (!StpUtil.isLogin()) {
            throw new BusinessException(ErrorCode.NO_LOGIN);
        }
        // 获取当前用户ID
        long userId = StpUtil.getLoginIdAsLong();

        return convertVoFromUserInfo(userId);
    }

    @Override
    public UserInfoVO updateCurrentUser(UserUpdateRequest userUpdateRequest) {
        long userId = StpUtil.getLoginIdAsLong();
        // 检查是否已登录
        if (!StpUtil.isLogin()) {
            throw new BusinessException(ErrorCode.NO_LOGIN);
        }
        // 参数校验
        if (userUpdateRequest == null) {
            throw new BusinessException(ErrorCode.BAD_PARAMS);
        }
        // 调用请求体自身的校验方法
        userUpdateRequest.check();
        // 获取当前用户ID
        UserInfo updateUserInfo = getUpdateUserInfo(userUpdateRequest, userId);
        // 使用MyBatis-Plus的updateById方法更新
        boolean updateResult = this.updateById(updateUserInfo);
        if (!updateResult) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "更新用户信息失败");
        }

        return convertVoFromUserInfo(userId);
    }

    @Override
    public UserInfoVO adminUpdateUser(Long userId, UserUpdateRequest userUpdateRequest) {
        // 检查是否已登录
        if (!StpUtil.isLogin()) {
            throw new BusinessException(ErrorCode.NO_LOGIN);
        }
        // 参数校验
        if (userUpdateRequest == null || userId == null) {
            throw new BusinessException(ErrorCode.BAD_PARAMS);
        }

        // 调用请求体自身的校验方法
        userUpdateRequest.check();
        UserInfo userInfo = this.getById(userId);
        if (userInfo == null) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "用户不存在");
        }
        if (!StpUtil.hasRole(UserRoleEnum.ADMIN.getValue())
                && UserRoleEnum.TEACHER.getLabel() <= userInfo.getRole().getLabel()) {
            log.warn("当前用户角色为 {} ,没有权限", UserRoleEnum.TEACHER.getDescription());
            throw new BusinessException(ErrorCode.NO_AUTHORITY_ALTER, "没有权限修改当前用户信息！");
        }

        UserInfo updateUserInfo = getUpdateUserInfo(userUpdateRequest, userId);
        boolean updateResult = this.updateById(updateUserInfo);
        if (!updateResult) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "更新用户信息失败");
        }

        return convertVoFromUserInfo(userId);
    }

    @Override
    public PageResponse<UserInfoVO> getAllUsers(PageRequest pageRequest) {
        // 参数校验和默认值设置
        if (pageRequest == null) {
            pageRequest = new PageRequest();
        }
        
        int pageNum = pageRequest.getPageNum() != null ? pageRequest.getPageNum() : 1;
        int pageSize = pageRequest.getPageSize() != null ? pageRequest.getPageSize() : 10;
        
        // 确保页码和每页大小合法
        pageNum = Math.max(1, pageNum);
        pageSize = Math.clamp(pageSize, 1, 100);
        
        // 查询总记录数
        LambdaQueryWrapper<UserInfo> countWrapper = new LambdaQueryWrapper<>();
        Long total = this.count(countWrapper);
        
        // 分页查询用户信息，按 ID 升序
        LambdaQueryWrapper<UserInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByAsc(UserInfo::getId);
        
        // 计算偏移量和限制
        int offset = (pageNum - 1) * pageSize;
        queryWrapper.last("LIMIT " + pageSize + " OFFSET " + offset);
        
        List<UserInfo> userList = this.list(queryWrapper);
        
        // 转换为 VO 对象
        List<UserInfoVO> userInfoVOList = new ArrayList<>(userList.size());
        for (UserInfo userInfo : userList) {
            UserInfoVO userInfoVO = new UserInfoVO();
            BeanUtils.copyProperties(userInfo, userInfoVO);
            String avatarUrl = userInfo.getAvatar();
            if (StringUtils.isNotBlank(avatarUrl)) {
                userInfoVO.setAvatar(minioService.getFileUrl(avatarUrl));
            }
            userInfoVOList.add(userInfoVO);
        }

        // todo 根据传入特定字段信息去过滤实现查询分页
        
        // 构建分页响应
        return new PageResponse<>(pageNum, pageSize, total, userInfoVOList);
    }

    @Override
    public boolean deleteUser(Long userId) {
        // 参数校验
        if (userId == null) {
            throw new BusinessException(ErrorCode.BAD_PARAMS);
        }
        
        // 查询用户是否存在
        UserInfo userInfo = this.getById(userId);
        if (userInfo == null) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "用户不存在");
        }
        
        // 检查当前登录用户是否有权限删除该用户
        Long currentUserId = StpUtil.getLoginIdAsLong();
        if (!currentUserId.equals(userId)) {
            // 如果不是删除自己，需要检查权限
            UserInfo currentUser = this.getById(currentUserId);
            if (currentUser == null) {
                throw new BusinessException(ErrorCode.NO_LOGIN);
            }
            // 不能删除比自己权限高或者同级别的用户
            if (!StpUtil.hasRole(UserRoleEnum.ADMIN.getValue())
                    && UserRoleEnum.TEACHER.getLabel() <= userInfo.getRole().getLabel()) {
                log.warn("当前用户角色为 {}, 没有权限删除其他用户", UserRoleEnum.TEACHER.getDescription());
                throw new BusinessException(ErrorCode.NO_AUTHORITY_ALTER, "没有权限删除当前用户信息！");
            }
        }
        
        // 软删除：将状态设置为 1（封禁）
        userInfo.setStatus(1);
        boolean updateResult = this.updateById(userInfo);
        
        if (!updateResult) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "删除用户失败");
        }
        
        log.info("用户已被软删除：userId={}, username={}", userId, userInfo.getUsername());
        return true;
    }

    @Override
    public UserInfoVO updateUserAvatar(Long userId, MultipartFile file) {

        UserInfo userInfo = this.getById(userId);
        if (userInfo == null) {
            throw new BusinessException(ErrorCode.RESOURCE_NO_USERINFO, "用户不存在");
        }
        String oldAvatar = userInfo.getAvatar();

        // 调用 MinIO 服务上传文件
        String avatarUrl = minioService.uploadFile(file, "avatar");

        // 更新当前用户的头像信息
        UserUpdateRequest userUpdateRequest = new UserUpdateRequest();
        userUpdateRequest.setAvatar(avatarUrl);
        UserInfoVO updateUser = this.adminUpdateUser(userId, userUpdateRequest);
        if (updateUser == null) {
            // 如果更新失败，则删除上传的图片文件
            minioService.removeFile(avatarUrl);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "更新用户头像失败");
        }
        // 删除老的头像文件
        minioService.removeFile(oldAvatar);

        return updateUser;
    }

    private UserInfo getUpdateUserInfo(UserUpdateRequest userUpdateRequest, Long userId) {
        // 构造更新条件
        UserInfo updateUserInfo = new UserInfo();
        updateUserInfo.setId(userId);

        // 设置需要更新的字段（只设置非null字段）
        if (StringUtils.isNotBlank(userUpdateRequest.getAvatar())) {
            updateUserInfo.setAvatar(userUpdateRequest.getAvatar());
        }
        if (userUpdateRequest.getGender() != null) {
            updateUserInfo.setGender(userUpdateRequest.getGender());
        }
        if (StringUtils.isNotBlank(userUpdateRequest.getSchool())) {
            updateUserInfo.setSchool(userUpdateRequest.getSchool());
        }
        if (StringUtils.isNotBlank(userUpdateRequest.getSignature())) {
            updateUserInfo.setSignature(userUpdateRequest.getSignature());
        }
        if (StringUtils.isNotBlank(userUpdateRequest.getPassword())) {
            updateUserInfo.setPassword(PasswordEncoderUtil.encode(userUpdateRequest.getPassword()));
        }
        LambdaQueryWrapper<UserInfo> queryWrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotBlank(userUpdateRequest.getUsername())) {
            // 校验用户名是否重复
            queryWrapper.eq(UserInfo::getUsername, userUpdateRequest.getUsername());
            queryWrapper.ne(UserInfo::getId, userId);
            long count = this.count(queryWrapper);
            if (count > 0) {
                throw new BusinessException(ErrorCode.BAD_PARAMS, "用户名已存在！");
            }
            updateUserInfo.setUsername(userUpdateRequest.getUsername());
        }
        // 校验手机号是否重复
        if (StringUtils.isNotBlank(userUpdateRequest.getPhone())) {
            queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(UserInfo::getPhone, userUpdateRequest.getPhone());
            queryWrapper.ne(UserInfo::getId, userId);
            long count = this.count(queryWrapper);
            if (count > 0) {
                throw new BusinessException(ErrorCode.BAD_PARAMS, "手机号已被绑定！");
            }
            updateUserInfo.setPhone(userUpdateRequest.getPhone());
        }

        // 校验邮箱是否重复
        if (StringUtils.isNotBlank(userUpdateRequest.getEmail())) {
            queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(UserInfo::getEmail, userUpdateRequest.getEmail());
            queryWrapper.ne(UserInfo::getId, userId);
            long count = this.count(queryWrapper);
            if (count > 0) {
                throw new BusinessException(ErrorCode.BAD_PARAMS, "邮箱已被绑定！");
            }
            updateUserInfo.setEmail(userUpdateRequest.getEmail());
        }
        return updateUserInfo;
    }

    private UserInfoVO convertVoFromUserInfo(Long userId) {
        UserInfo userInfo = this.getById(userId);
        if (userInfo == null) {
            throw new BusinessException(ErrorCode.RESOURCE_NO_USERINFO, "用户不存在");
        }
        UserInfoVO userInfoVO = new UserInfoVO();
        BeanUtils.copyProperties(userInfo, userInfoVO);
        String avatarUrl = userInfo.getAvatar();
        if (StringUtils.isNotBlank(avatarUrl)) {
            userInfoVO.setAvatar(minioService.getFileUrl(avatarUrl));
        }
        return userInfoVO;
    }
}
