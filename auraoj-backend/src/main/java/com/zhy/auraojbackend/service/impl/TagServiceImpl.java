package com.zhy.auraojbackend.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhy.auraojbackend.common.ErrorCode;
import com.zhy.auraojbackend.exception.BusinessException;
import com.zhy.auraojbackend.exception.ThrowUtils;
import com.zhy.auraojbackend.mapper.TagMapper;
import com.zhy.auraojbackend.model.dto.tag.request.TagAddRequest;
import com.zhy.auraojbackend.model.dto.tag.request.TagUpdateRequest;
import com.zhy.auraojbackend.model.entity.TagInfo;
import com.zhy.auraojbackend.service.TagService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
* @author zhy
* @description 针对表【tag(标签表（知识点/来源）)】的数据库操作 Service 实现
* @createDate 2026-03-15 17:19:29
*/
@Slf4j
@Service
public class TagServiceImpl extends ServiceImpl<TagMapper, TagInfo>
    implements TagService {

    @Override
    public List<TagInfo> listAllTags(String tagName) {
        log.info("查询所有标签，tagName: {}", tagName);
        
        // 构建查询条件
        LambdaQueryWrapper<TagInfo> queryWrapper = new LambdaQueryWrapper<>();
        
        // 如果提供了标签名，进行模糊查询
        if (StringUtils.isNotBlank(tagName)) {
            queryWrapper.like(TagInfo::getName, tagName);
        }
        
        return this.list(queryWrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long addTag(TagAddRequest tagAddRequest) {
        // 1. 参数校验
        try {
            tagAddRequest.check();
        } catch (IllegalArgumentException e) {
            throw new BusinessException(ErrorCode.BAD_PARAMS, e.getMessage());
        }

        // 2. 构建 Tag 实体
        TagInfo tag = new TagInfo();
        tag.setName(tagAddRequest.getName());
        tag.setClassification(tagAddRequest.getClassification());
        tag.setCreatorId(StpUtil.getLoginIdAsLong());
        
        // 3. 保存到数据库
        boolean result = this.save(tag);
        ThrowUtils.throwIf(!result, ErrorCode.SYSTEM_ERROR, "添加标签失败");
        
        log.info("新增标签成功，标签 ID: {}", tag.getId());
        return tag.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TagInfo updateTag(TagUpdateRequest tagUpdateRequest) {
        // 1. 参数校验
        try {
            tagUpdateRequest.check();
        } catch (IllegalArgumentException e) {
            throw new BusinessException(ErrorCode.BAD_PARAMS, e.getMessage());
        }

        // 2. 检查标签是否存在
        TagInfo existTag = this.getById(tagUpdateRequest.getId());
        ThrowUtils.throwIf(existTag == null, ErrorCode.RESOURCE_NO_TAG, "标签不存在");
        
        // 3. 更新 Tag 实体
        TagInfo tag = new TagInfo();
        tag.setId(tagUpdateRequest.getId());
        tag.setName(tagUpdateRequest.getName());
        tag.setClassification(tagUpdateRequest.getClassification());
        
        boolean result = this.updateById(tag);
        ThrowUtils.throwIf(!result, ErrorCode.SYSTEM_ERROR, "更新标签失败");
        
        log.info("更新标签成功，标签 ID: {}", tag.getId());
        return this.getById(tag.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteTag(Long id) {
        ThrowUtils.throwIf(id == null, ErrorCode.BAD_PARAMS, "标签 ID 不能为空");
        
        // 检查标签是否存在
        TagInfo existTag = this.getById(id);
        ThrowUtils.throwIf(existTag == null, ErrorCode.RESOURCE_NO_TAG, "标签不存在");
        
        boolean result = this.removeById(id);
        ThrowUtils.throwIf(!result, ErrorCode.SYSTEM_ERROR, "删除标签失败");
        
        log.info("删除标签成功，标签 ID: {}", id);
        return true;
    }
}
