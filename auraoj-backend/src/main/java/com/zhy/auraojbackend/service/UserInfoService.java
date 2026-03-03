package com.zhy.auraojbackend.service;

import com.zhy.auraojbackend.model.entity.UserInfo;
import com.zhy.auraojbackend.model.dto.user.UserLoginRequest;
import com.zhy.auraojbackend.model.dto.user.UserLoginResponse;
import com.zhy.auraojbackend.model.dto.user.UserRegisterRequest;
import com.zhy.auraojbackend.model.dto.user.UserUpdateRequest;
import com.baomidou.mybatisplus.extension.service.IService;

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
    UserInfo getCurrentUser();

    /**
     * 更新当前登录用户信息
     *
     * @param userUpdateRequest 更新请求参数
     * @return 是否更新成功
     */
    boolean updateCurrentUser(UserUpdateRequest userUpdateRequest);

    /**
     * 管理员更新用户信息
     *
     * @param userId            用户ID
     * @param userUpdateRequest 更新请求参数
     * @return 是否更新成功
     */
    boolean adminUpdateUser(Long userId, UserUpdateRequest userUpdateRequest);
}
