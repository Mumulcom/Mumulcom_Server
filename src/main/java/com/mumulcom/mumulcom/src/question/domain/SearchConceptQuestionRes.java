package com.mumulcom.mumulcom.src.question.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class SearchConceptQuestionRes {
    private long questionIdx;
    private String profileImgUrl;
    private String nickname;
    private String bigCategoryName;
    private String smallCategoryName;
    private String title;
    private String createdAt;
    private String content;
    private int likeCount;
    private int replyCount;
}
