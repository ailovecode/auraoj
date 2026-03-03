package com.zhy.auraojbackend.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhy.auraojbackend.common.ErrorCode;
import com.zhy.auraojbackend.exception.BusinessException;
import com.zhy.auraojbackend.mapper.UserInfoMapper;
import com.zhy.auraojbackend.model.dto.user.UserLoginRequest;
import com.zhy.auraojbackend.model.dto.user.UserLoginResponse;
import com.zhy.auraojbackend.model.dto.user.UserRegisterRequest;
import com.zhy.auraojbackend.model.dto.user.UserUpdateRequest;
import com.zhy.auraojbackend.model.entity.UserInfo;
import com.zhy.auraojbackend.model.enums.UserRoleEnum;
import com.zhy.auraojbackend.service.UserInfoService;
import com.zhy.auraojbackend.utils.PasswordEncoderUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.Date;
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
            throw new BusinessException(ErrorCode.ACCESS_DENIED, "账号已被封禁");
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
    public UserInfo getCurrentUser() {
        // 检查是否已登录
        if (!StpUtil.isLogin()) {
            throw new BusinessException(ErrorCode.NO_LOGIN);
        }

        // 获取当前用户ID
        long userId = StpUtil.getLoginIdAsLong();
        
        // 查询用户信息
        UserInfo currentUser = this.getById(userId);
        if (currentUser == null) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "用户不存在");
        }

        // 脱敏处理，不返回密码等敏感信息
        currentUser.setPassword(null);
        return currentUser;
    }

    @Override
    public boolean updateCurrentUser(UserUpdateRequest userUpdateRequest) {
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
        UserInfo updateUserInfo = getUpdateUserInfo(userUpdateRequest, null);
        // 使用MyBatis-Plus的updateById方法更新
        boolean updateResult = this.updateById(updateUserInfo);
        if (!updateResult) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "更新用户信息失败");
        }

        return true;
    }

    @Override
    public boolean adminUpdateUser(Long userId, UserUpdateRequest userUpdateRequest) {
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

        return true;
    }

    private UserInfo getUpdateUserInfo(UserUpdateRequest userUpdateRequest, Long userId) {
        if (userId == null) {
            userId = StpUtil.getLoginIdAsLong();
        }

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
            long count = this.count(queryWrapper);
            if (count > 0) {
                throw new BusinessException(ErrorCode.BAD_PARAMS, "邮箱已被绑定！");
            }
            updateUserInfo.setEmail(userUpdateRequest.getEmail());
        }
        return updateUserInfo;
    }
}
