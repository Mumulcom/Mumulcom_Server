package com.mumulcom.mumulcom.src.like.controller;

import com.mumulcom.mumulcom.config.BaseException;
import com.mumulcom.mumulcom.config.BaseResponse;

import com.mumulcom.mumulcom.config.BaseResponseStatus;
import com.mumulcom.mumulcom.src.like.service.QuestionLikeService;
import com.mumulcom.mumulcom.src.like.provider.QuestionLikeProvider;
import com.mumulcom.mumulcom.src.like.dto.PostQueLikeReq;
import com.mumulcom.mumulcom.src.like.dto.PostReplyLikeReq;
import com.mumulcom.mumulcom.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import static com.mumulcom.mumulcom.config.BaseResponseStatus.*;

@RestController

@RequestMapping("/likes")

public class QuestionLikeController {

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private final QuestionLikeProvider questionLikeProvider;
    @Autowired
    private final QuestionLikeService questionLikeService;
    @Autowired
    private final JwtService jwtService;

    public QuestionLikeController(QuestionLikeProvider questionLikeProvider, QuestionLikeService questionLikeService, JwtService jwtService){
        this.questionLikeProvider = questionLikeProvider;
        this.questionLikeService = questionLikeService;
        this.jwtService = jwtService;
    }

    //학준 25.질문에 좋아요하기+알림넣기

    @ResponseBody
    @PostMapping("/questions/creation")
    @Transactional(rollbackFor = Exception.class)
    public BaseResponse<String> createQueLike(@RequestBody PostQueLikeReq postQueLikeReq){

        try{
            Long userIdxByJwt = jwtService.getUserIdx();
            if (!userIdxByJwt.equals(postQueLikeReq.getUserIdx())) {
                throw new BaseException(BaseResponseStatus.INVALID_JWT);
            }
            if(postQueLikeReq.getQuestionIdx() == 0 || postQueLikeReq.getUserIdx() == 0 )
                throw new BaseException(POST_EMPTY_ESSENTIAL_BODY);
            String result = questionLikeService.createQuestionLike(postQueLikeReq);
            return new BaseResponse<>(result);
        }catch (BaseException exception){
            exception.printStackTrace();
            return new BaseResponse<>(exception.getStatus());
        }
    }

    //학준 26. 답변좋아요+ 알림넣기
    @ResponseBody
    @PostMapping("/replies/creation")
    public BaseResponse<String> createReplyLike(@RequestBody PostReplyLikeReq postReplyLikeReq){

        try{
            Long userIdxByJwt = jwtService.getUserIdx();
            if (!userIdxByJwt.equals(postReplyLikeReq.getUserIdx())) {
                throw new BaseException(BaseResponseStatus.INVALID_JWT);
            }

            if(postReplyLikeReq.getReplyIdx() == 0 || postReplyLikeReq.getUserIdx() == 0 )
                throw new BaseException(POST_EMPTY_ESSENTIAL_BODY);
            String result = questionLikeService.createReplyLike(postReplyLikeReq);
            return new BaseResponse<>(result);
        }catch (BaseException exception){
            exception.printStackTrace();
            return new BaseResponse<>(exception.getStatus());
        }
    }
}