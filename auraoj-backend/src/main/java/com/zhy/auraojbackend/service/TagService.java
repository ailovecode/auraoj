package com.zhy.auraojbackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhy.auraojbackend.model.dto.tag.request.TagAddRequest;
import com.zhy.auraojbackend.model.dto.tag.request.TagUpdateRequest;
import com.zhy.auraojbackend.model.entity.TagInfo;

import java.util.List;

/**
* @author zhy
* @description 针对表【tag(标签表（知识点/来源）)】的数据库操作 Service
* @createDate 2026-03-15 17:19:29
*/
public interface TagService extends IService<TagInfo> {

    /**
     * 查询所有标签
     * @param tagName 标签名（支持模糊查询）
     * @return 标签列表
     */
    List<TagInfo> listAllTags(String tagName);

    /**
     * 新增标签
     * @param tagAddRequest 新增标签请求参数
     * @return 标签 ID
     */
    Long addTag(TagAddRequest tagAddRequest);

    /**
     * 更新标签
     * @param tagUpdateRequest 更新标签请求参数
     * @return 是否更新成功
     */
    TagInfo updateTag(TagUpdateRequest tagUpdateRequest);

    /**
     * 删除标签
     * @param id 标签 ID
     * @return 是否删除成功
     */
    boolean deleteTag(Long id);
}
