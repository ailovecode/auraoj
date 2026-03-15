package com.zhy.auraojbackend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhy.auraojbackend.model.entity.ProblemTagMap;
import com.zhy.auraojbackend.service.ProblemTagMapService;
import com.zhy.auraojbackend.mapper.ProblemTagMapMapper;
import org.springframework.stereotype.Service;

/**
* @author zhy
* @description 针对表【problem_tag_map(题目与标签的多对多关联表)】的数据库操作Service实现
* @createDate 2026-03-15 17:48:09
*/
@Service
public class ProblemTagMapServiceImpl extends ServiceImpl<ProblemTagMapMapper, ProblemTagMap>
    implements ProblemTagMapService{

}




