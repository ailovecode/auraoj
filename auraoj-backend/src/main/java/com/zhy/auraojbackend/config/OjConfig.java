package com.zhy.auraojbackend.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * OJ 系统配置
 * @author zhy
 * @Date 2026/3/15
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "oj")
public class OjConfig {

    /**
     * 文件路径前缀
     * Windows 示例：G:\judge\data
     * Linux 示例：/home/judge/data
     */
    private String pathPrefix;
}
