package com.zhy.auraojbackend.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * @version 1.0
 * @Author zhy
 * @Date 2026/3/1
 */
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI sweggerOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("AuraOJ 后端接口文档")
                        .version("1.0.0")
                        .description("AuraOJ 在线判题系统后端 API 接口文档，基于 Spring Boot 3 + Knife4j")
                        .contact(new Contact()
                                .name("xiaoyu")
                                .email("dev@auraoj.com")
                                .url("https://github.com/ailovecode/auraoj"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://mit-license.org")))
                .servers(List.of(
                        new Server().url("http://localhost:8080").description("本地开发环境"),
                        new Server().url("https://api.auraoj.com").description("生产环境")
                ));
    }
}