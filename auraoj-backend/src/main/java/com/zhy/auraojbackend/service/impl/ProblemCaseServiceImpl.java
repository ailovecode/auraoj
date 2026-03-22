package com.zhy.auraojbackend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhy.auraojbackend.model.entity.ProblemCase;
import com.zhy.auraojbackend.service.ProblemCaseService;
import com.zhy.auraojbackend.mapper.ProblemCaseMapper;
import org.springframework.stereotype.Service;

/**
* @author zhy
* @description 针对表【problem_case(题目测试样例表（存储文件路径或内容摘要）)】的数据库操作Service实现
* @createDate 2026-03-22 21:13:10
*/
@Service
public class ProblemCaseServiceImpl extends ServiceImpl<ProblemCaseMapper, ProblemCase>
    implements ProblemCaseService{

}




