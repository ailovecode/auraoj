package com.zhy.auraojbackend.service;

import com.zhy.auraojbackend.model.entity.UserInfo;
import com.zhy.auraojbackend.model.dto.user.UserLoginRequest;
import com.zhy.auraojbackend.model.dto.user.UserLoginResponse;
import com.zhy.auraojbackend.model.dto.user.UserRegisterRequest;
import com.zhy.auraojbackend.model.dto.user.UserUpdateRequest;
import com.zhy.auraojbackend.model.vo.UserInfoVO;
import com.zhy.auraojbackend.model.dto.PageRequest;
import com.zhy.auraojbackend.model.dto.PageResponse;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;

/**
* @author zhy
* @description 针对表【user_info(用户信息表)】的数据库操作Service
* @createDate 2026-02-08 17:00:45
*/
public interface UserInfoService extends IService<UserInfo> {

    /**
     * 用户注册
     *
     * @param userRegisterRequest 注册请求参数
     * @return 用户ID
     */
    Long userRegister(UserRegisterRequest userRegisterRequest);

    /**
     * 用户登录
     *
     * @param userLoginRequest 登录请求参数
     * @return 登录响应信息
     */
    UserLoginResponse userLogin(UserLoginRequest userLoginRequest);

    /**
     * 用户登出
     *
     * @return 是否登出成功
     */
    boolean userLogout();

    /**
     * 获取当前登录用户信息
     *
     * @return 当前用户信息
     */
    UserInfoVO getCurrentUser();

    /**
     * 更新当前登录用户信息
     *
     * @param userUpdateRequest 更新请求参数
     * @return 是否更新成功
     */
    UserInfoVO updateCurrentUser(UserUpdateRequest userUpdateRequest);

    /**
     * 管理员更新用户信息
     *
     * @param userId            用户 ID
     * @param userUpdateRequest 更新请求参数
     * @return 是否更新成功
     */
    UserInfoVO adminUpdateUser(Long userId, UserUpdateRequest userUpdateRequest);

    /**
     * 获取所有用户信息（管理员和教师权限，分页）
     *
     * @param pageRequest 分页请求参数
     * @return 分页后的用户信息列表
     */
    PageResponse<UserInfoVO> getAllUsers(PageRequest pageRequest);

    /**
     * 删除用户（软删除，将状态设置为封禁）
     *
     * @param userId 用户 ID
     * @return 是否删除成功
     */
    boolean deleteUser(Long userId);

    /**
     * 更新用户头像
     *
     * @param userId  用户 ID
     * @param file    头像文件
     * @return 是否更新成功
     */
    UserInfoVO updateUserAvatar(Long userId, MultipartFile file);
}
