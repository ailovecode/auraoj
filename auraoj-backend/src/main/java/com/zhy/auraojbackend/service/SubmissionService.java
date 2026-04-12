package com.zhy.auraojbackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhy.auraojbackend.model.dto.PageResponse;
import com.zhy.auraojbackend.model.dto.submission.ShowSubmissionInfo;
import com.zhy.auraojbackend.model.dto.submission.request.ShowSubmissionRequest;
import com.zhy.auraojbackend.model.dto.submission.request.SubmitRequest;
import com.zhy.auraojbackend.model.dto.submission.request.TestCaseDebugRequest;
import com.zhy.auraojbackend.model.dto.submission.response.SubmitResponse;
import com.zhy.auraojbackend.model.dto.submission.response.TestCaseDebugResponse;
import com.zhy.auraojbackend.model.entity.Submission;

/**
* @author zhy
* @description Service for submission records.
* @createDate 2026-03-25 21:28:04
*/
public interface SubmissionService extends IService<Submission> {

    /**
     * Create a submission record.
     *
     * @param submitRequest submit request
     * @return compact submission info
     */
    SubmitResponse submit(SubmitRequest submitRequest);

    /**
     * Query submission detail.
     *
     * @param showSubmissionRequest query conditions
     * @return paged submission list
     */
    PageResponse<ShowSubmissionInfo> getSubmissionInfo(ShowSubmissionRequest showSubmissionRequest);

    /**
     * 在线调试：单测试点即时执行代码并返回结果
     *
     * @param debugRequest 调试请求
     * @return 调试结果
     */
    TestCaseDebugResponse testCaseDebug(TestCaseDebugRequest debugRequest);

}
