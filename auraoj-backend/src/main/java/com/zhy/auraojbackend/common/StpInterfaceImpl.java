package com.zhy.auraojbackend.common;

import cn.dev33.satoken.stp.StpInterface;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zhy.auraojbackend.model.entity.UserInfo;
import com.zhy.auraojbackend.model.enums.UserRoleEnum;
import com.zhy.auraojbackend.service.UserInfoService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @version 1.0
 * @Author zhy
 * @Date 2026/3/1
 */
@Component
public class StpInterfaceImpl implements StpInterface {

    @Resource
    private UserInfoService userInfoService;

    @Override
    public List<String> getPermissionList(Object o, String s) {
        return List.of();
    }

    @Override
    public List<String> getRoleList(Object loginId, String s) {
        QueryWrapper<UserInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.and(wrapper -> wrapper.eq("id", loginId));
        UserInfo userInfo = userInfoService.getOne(queryWrapper);
        List<String> list = new ArrayList<>();
        if (userInfo != null) {
            if (userInfo.getRole() == UserRoleEnum.ADMIN) {
                list.add(UserRoleEnum.ADMIN.getValue());
                list.add(UserRoleEnum.TEACHER.getValue());
                list.add(UserRoleEnum.STUDENT.getValue());
            } else if (userInfo.getRole() == UserRoleEnum.TEACHER) {
                list.add(UserRoleEnum.TEACHER.getValue());
                list.add(UserRoleEnum.STUDENT.getValue());
            } else if (userInfo.getRole() == UserRoleEnum.STUDENT) {
                list.add(UserRoleEnum.STUDENT.getValue());
            }
        }
        return list;
    }
}
