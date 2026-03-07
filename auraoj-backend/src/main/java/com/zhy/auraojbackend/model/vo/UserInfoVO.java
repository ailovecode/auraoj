package com.zhy.auraojbackend.model.vo;

import com.zhy.auraojbackend.model.dto.BaseUserInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;

/**
 * 用户信息视图对象（用于返回给前端，不包含敏感信息）
 *
 * @author zhy
 * @date 2026/3/7
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class UserInfoVO extends BaseUserInfo implements Serializable  {

    @Serial
    private static final long serialVersionUID = 1L;

}
