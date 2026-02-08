package com.zhy.auraojbackend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhy.auraojbackend.model.entity.UserInfo;
import com.zhy.auraojbackend.service.UserInfoService;
import com.zhy.auraojbackend.mapper.UserInfoMapper;
import org.springframework.stereotype.Service;

/**
* @author DELL
* @description 针对表【user_info(用户信息表)】的数据库操作Service实现
* @createDate 2026-02-08 17:00:45
*/
@Service
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo>
    implements UserInfoService{

}




