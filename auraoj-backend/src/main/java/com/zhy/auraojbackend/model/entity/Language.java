package com.zhy.auraojbackend.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 语言类型
 * @author zhy
 * @TableName language
 */
@TableName(value ="public.language")
@Data
public class Language {
    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 代码语言名称（前端显示的样式）
     */
    private String name;

    /**
     * 语言名称（Monaco编辑器中的名称）
     */
    private String monacoName;

    /**
     * 代码模版
     */
    private String codeTemplate;

    /**
     * 相关描述
     */
    private String description;

    /**
     * 创建时间
     */
    private Date gmtCreate;

    /**
     * 修改时间
     */
    private Date gmtModified;
}