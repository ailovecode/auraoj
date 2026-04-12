package com.zhy.auraojbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhy.auraojbackend.common.ErrorCode;
import com.zhy.auraojbackend.exception.ThrowUtils;
import com.zhy.auraojbackend.mapper.LanguageMapper;
import com.zhy.auraojbackend.model.dto.language.request.LanguageAddRequest;
import com.zhy.auraojbackend.model.dto.language.request.LanguageQueryRequest;
import com.zhy.auraojbackend.model.dto.language.request.LanguageUpdateRequest;
import com.zhy.auraojbackend.model.dto.language.response.LanguageResponse;
import com.zhy.auraojbackend.model.dto.PageResponse;
import com.zhy.auraojbackend.model.entity.Language;
import com.zhy.auraojbackend.service.LanguageService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
* @author zhy
* @description 针对表【language(语言类型)】的数据库操作Service实现
* @createDate 2026-04-12 16:18:50
*/
@Service
@Slf4j
public class LanguageServiceImpl extends ServiceImpl<LanguageMapper, Language>
    implements LanguageService{

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long addLanguage(LanguageAddRequest request) {
        // 参数校验
        request.check();
        
        // 检查语言名称是否已存在
        LambdaQueryWrapper<Language> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Language::getName, request.getName());
        Long count = this.count(queryWrapper);
        ThrowUtils.throwIf(count > 0, ErrorCode.BAD_PARAMS, "语言名称已存在");
        
        // 构建实体对象
        Language language = new Language();
        BeanUtils.copyProperties(request, language);
        
        // 保存到数据库
        boolean result = this.save(language);
        ThrowUtils.throwIf(!result, ErrorCode.SYSTEM_ERROR, "添加语言失败");
        
        log.info("添加代码语言成功，语言 ID: {}, 名称: {}", language.getId(), language.getName());
        return language.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Language updateLanguage(LanguageUpdateRequest request) {
        // 参数校验
        request.check();
        
        // 检查语言是否存在
        Language oldLanguage = this.getById(request.getId());
        ThrowUtils.throwIf(oldLanguage == null, ErrorCode.RESOURCE_NOT_FOUND, "语言不存在");
        
        // 如果修改了名称，检查新名称是否已被使用
        if (StringUtils.isNotBlank(request.getName()) && !request.getName().equals(oldLanguage.getName())) {
            LambdaQueryWrapper<Language> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Language::getName, request.getName());
            queryWrapper.ne(Language::getId, request.getId());
            Long count = this.count(queryWrapper);
            ThrowUtils.throwIf(count > 0, ErrorCode.BAD_PARAMS, "语言名称已存在");
        }
        
        // 更新字段
        Language updateLanguage = new Language();
        BeanUtils.copyProperties(request, updateLanguage);
        
        boolean result = this.updateById(updateLanguage);
        ThrowUtils.throwIf(!result, ErrorCode.SYSTEM_ERROR, "更新语言失败");
        
        // 获取更新后的完整信息
        Language updatedLanguage = this.getById(request.getId());
        log.info("更新代码语言成功，语言 ID: {}", updatedLanguage.getId());
        
        return updatedLanguage;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteLanguage(Long id) {
        // 参数校验
        ThrowUtils.throwIf(id == null || id <= 0, ErrorCode.BAD_PARAMS, "语言 ID 无效");
        
        // 检查语言是否存在
        Language language = this.getById(id);
        ThrowUtils.throwIf(language == null, ErrorCode.RESOURCE_NOT_FOUND, "语言不存在");
        
        // 删除语言
        boolean result = this.removeById(id);
        ThrowUtils.throwIf(!result, ErrorCode.SYSTEM_ERROR, "删除语言失败");
        
        log.info("删除代码语言成功，语言 ID: {}", id);
        return true;
    }

    @Override
    public PageResponse<LanguageResponse> listLanguages(LanguageQueryRequest request) {
        // 构建查询条件
        LambdaQueryWrapper<Language> queryWrapper = new LambdaQueryWrapper<>();
        
        // 按语言名称模糊查询
        if (StringUtils.isNotBlank(request.getName())) {
            queryWrapper.like(Language::getName, request.getName());
        }
        
        // 设置排序
        String sortField = request.getSortField();
        String sortOrder = request.getSortOrder();
        if (StringUtils.isNotBlank(sortField)) {
            if ("asc".equalsIgnoreCase(sortOrder)) {
                queryWrapper.orderByAsc(getOrderColumn(sortField));
            } else {
                queryWrapper.orderByDesc(getOrderColumn(sortField));
            }
        } else {
            // 默认按 ID 升序
            queryWrapper.orderByAsc(Language::getId);
        }
        
        // 分页查询
        Page<Language> page = new Page<>(request.getPageNum(), request.getPageSize());
        Page<Language> languagePage = this.page(page, queryWrapper);
        
        // 转换为响应 DTO
        List<LanguageResponse> records = languagePage.getRecords().stream()
                .map(language -> LanguageResponse.builder()
                        .id(language.getId())
                        .name(language.getName())
                        .monacoName(language.getMonacoName())
                        .codeTemplate(language.getCodeTemplate())
                        .description(language.getDescription())
                        .gmtCreate(language.getGmtCreate())
                        .gmtModified(language.getGmtModified())
                        .build())
                .collect(Collectors.toList());
        
        // 构建分页响应
        PageResponse<LanguageResponse> response = new PageResponse<>();
        response.setList(records);
        response.setTotal(languagePage.getTotal());
        response.setPageNum((int) languagePage.getCurrent());
        response.setPageSize((int) languagePage.getSize());
        response.setTotalPages((int) languagePage.getPages());
        
        return response;
    }

    @Override
    public LanguageResponse getLanguageById(Long id) {
        // 参数校验
        ThrowUtils.throwIf(id == null || id <= 0, ErrorCode.BAD_PARAMS, "语言 ID 无效");
        
        // 查询语言
        Language language = this.getById(id);
        ThrowUtils.throwIf(language == null, ErrorCode.RESOURCE_NOT_FOUND, "语言不存在");
        
        // 转换为响应 DTO
        return LanguageResponse.builder()
                .id(language.getId())
                .name(language.getName())
                .monacoName(language.getMonacoName())
                .codeTemplate(language.getCodeTemplate())
                .description(language.getDescription())
                .gmtCreate(language.getGmtCreate())
                .gmtModified(language.getGmtModified())
                .build();
    }

    /**
     * 根据排序字段获取对应的列
     * @param sortField 排序字段
     * @return 对应的列
     */
    private com.baomidou.mybatisplus.core.toolkit.support.SFunction<Language, ?> getOrderColumn(String sortField) {
        return switch (sortField.toLowerCase()) {
            case "name" -> Language::getName;
            case "monaconame", "monaco_name" -> Language::getMonacoName;
            case "gmtcreate", "gmt_create" -> Language::getGmtCreate;
            case "gmtmodified", "gmt_modified" -> Language::getGmtModified;
            default -> Language::getId;
        };
    }
}




