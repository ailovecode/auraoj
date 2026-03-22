package com.zhy.auraojbackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhy.auraojbackend.model.dto.problemcase.request.SingleProblemCaseUploadRequest;
import com.zhy.auraojbackend.model.dto.problemcase.response.ProblemCaseDeleteResponse;
import com.zhy.auraojbackend.model.entity.ProblemCase;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @author zhy
 */
public interface ProblemCaseService extends IService<ProblemCase> {

    List<ProblemCase> listByProblemId(Long problemId);

    List<ProblemCase> uploadZip(Long problemId, MultipartFile zipFile);

    ProblemCase uploadSingleCase(SingleProblemCaseUploadRequest request);

    ProblemCaseDeleteResponse deleteCaseFile(Long problemId, String fileName);

    Resource loadCaseFileAsResource(Long problemId, String fileName);
}