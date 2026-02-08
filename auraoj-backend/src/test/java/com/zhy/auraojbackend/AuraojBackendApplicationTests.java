package com.zhy.auraojbackend;

import com.zhy.auraojbackend.mapper.UserInfoMapper;
import com.zhy.auraojbackend.model.entity.UserInfo;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

@SpringBootTest
class AuraojBackendApplicationTests {

    private static final Logger log = LoggerFactory.getLogger(AuraojBackendApplicationTests.class);
    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    UserInfoMapper userInfoMapper;

    @Test
    void contextLoads() {
        UserInfo zhy = userInfoMapper.findUser("zhy");
    }

}
