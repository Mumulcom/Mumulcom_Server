package com.mumulcom.mumulcom.src.report.controller;

import com.mumulcom.mumulcom.config.BaseException;
import com.mumulcom.mumulcom.config.BaseResponse;
import com.mumulcom.mumulcom.config.BaseResponseStatus;
import com.mumulcom.mumulcom.src.report.dto.PostReportReq;
import com.mumulcom.mumulcom.src.report.dto.PostReportRes;
import com.mumulcom.mumulcom.src.report.service.ReportService;
import com.mumulcom.mumulcom.utils.JwtService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import static com.mumulcom.mumulcom.config.BaseResponseStatus.INVALID_USER_JWT;

@RequiredArgsConstructor
@RestController
@RequestMapping("/reports")
public class ReportController {

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final ReportService reportService;
    private final JwtService jwtService;

    /**
     * [POST] /reports
     */
    @ResponseBody
    @PostMapping("")
    public BaseResponse<PostReportRes> postReport(@RequestBody PostReportReq postReportReq) {
        try {
            Long userIdxByJwt = jwtService.getUserIdx();

            if(postReportReq.getReporterUserIdx() != userIdxByJwt) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }

            PostReportRes reportIdx = reportService.createReport(postReportReq);

            return new BaseResponse<>(reportIdx);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }
}
