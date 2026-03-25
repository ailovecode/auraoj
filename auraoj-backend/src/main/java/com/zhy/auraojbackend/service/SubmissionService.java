package com.zhy.auraojbackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhy.auraojbackend.model.dto.submission.request.SubmitRequest;
import com.zhy.auraojbackend.model.dto.submission.response.SubmitResponse;
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


}