package com.zhy.auraojbackend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @version 1.0
 * @Author zhy
 * @Date 2026/2/13 21:58
 */
@RestController
@RequestMapping("/api/user")
@Tag(name = "用户管理", description = "用户管理接口")
public class UserController {
    @GetMapping("/test")
    @Operation(summary = "测试", description = "看下接口文档是否生效")
    public String test() {
        return "test";
    }
}
