package com.zhy.auraojbackend;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.zhy.auraojbackend.mapper")
public class AuraojBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuraojBackendApplication.class, args);
    }

}
