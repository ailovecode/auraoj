package com.zhy.auraojbackend.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * @version 1.0
 * @Author zhy
 * @Date 2026/3/1
 */
@Slf4j
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

    // 定义中国时区
    private static final ZoneId CHINA_ZONE = ZoneId.of("Asia/Shanghai");

    @Override
    public void insertFill(MetaObject metaObject) {
        log.info("开始插入填充...");
        // 自动填充 createTime 和 updateTime
        this.strictInsertFill(metaObject, "gmtCreate", LocalDateTime.class, LocalDateTime.now(CHINA_ZONE));
        this.strictInsertFill(metaObject, "gmtModified", LocalDateTime.class, LocalDateTime.now(CHINA_ZONE));
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        log.info("开始更新填充...");
        // 自动填充 updateTime
        this.strictUpdateFill(metaObject, "gmtModified", LocalDateTime.class, LocalDateTime.now(CHINA_ZONE));
    }
}
