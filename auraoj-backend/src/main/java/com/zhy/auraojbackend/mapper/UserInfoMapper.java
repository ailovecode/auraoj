package com.zhy.auraojbackend.mapper;

import com.zhy.auraojbackend.model.entity.UserInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
* @author zhy
* @description 针对表【user_info(用户信息表)】的数据库操作Mapper
* @createDate 2026-02-08 17:00:45
* @Entity com.zhy.auraojbackend.model.entity.UserInfo
*/
public interface UserInfoMapper extends BaseMapper<UserInfo> {

    @Select("select * from public.user_info where username = #{username}")
    UserInfo findUser(@Param("username") String username);
}




