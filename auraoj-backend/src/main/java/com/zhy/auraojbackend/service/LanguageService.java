package com.zhy.auraojbackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhy.auraojbackend.model.dto.PageResponse;
import com.zhy.auraojbackend.model.dto.language.request.LanguageAddRequest;
import com.zhy.auraojbackend.model.dto.language.request.LanguageQueryRequest;
import com.zhy.auraojbackend.model.dto.language.request.LanguageUpdateRequest;
import com.zhy.auraojbackend.model.dto.language.response.LanguageResponse;
import com.zhy.auraojbackend.model.entity.Language;

/**
* @author zhy
* @description 针对表【language(语言类型)】的数据库操作Service
* @createDate 2026-04-12 16:18:50
*/
public interface LanguageService extends IService<Language> {

    /**
     * 新增代码语言
     * @param request 新增请求参数
     * @return 语言 ID
     */
    Long addLanguage(LanguageAddRequest request);

    /**
     * 更新代码语言
     * @param request 更新请求参数
     * @return 更新后的语言信息
     */
    Language updateLanguage(LanguageUpdateRequest request);

    /**
     * 删除代码语言
     * @param id 语言 ID
     * @return 是否删除成功
     */
    boolean deleteLanguage(Long id);

    /**
     * 分页查询代码语言列表
     * @param request 查询请求参数
     * @return 分页结果
     */
    PageResponse<LanguageResponse> listLanguages(LanguageQueryRequest request);

    /**
     * 根据 ID 查询语言详情
     * @param id 语言 ID
     * @return 语言详情
     */
    LanguageResponse getLanguageById(Long id);
}
