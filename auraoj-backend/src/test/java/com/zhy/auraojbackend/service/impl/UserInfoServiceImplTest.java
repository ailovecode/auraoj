package com.zhy.auraojbackend.service.impl;

import com.zhy.auraojbackend.model.dto.user.request.UserRegisterRequest;
import com.zhy.auraojbackend.model.entity.UserInfo;
import com.zhy.auraojbackend.service.UserInfoService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

/**
 * UserInfoServiceImpl 用户注册功能单元测试
 *
 * @author zhy
 * @date 2026/3/1
 */
@Slf4j
@SpringBootTest
class UserInfoServiceImplTest {

    @Resource
    UserInfoService userInfoService;

    @Test
    void testUserRegister() {
        UserRegisterRequest userInfo = new UserRegisterRequest();
        userInfo.setUsername("test3");
        userInfo.setPassword("123456");
        userInfo.setConfirmPassword("123456");

        Long userId = userInfoService.userRegister(userInfo);
        Optional<UserInfo> optById = userInfoService.getOptById(userId);
        log.info("用户注册成功，用户ID：{}", userId);
        log.info("用户信息：{}", optById);
    }
}