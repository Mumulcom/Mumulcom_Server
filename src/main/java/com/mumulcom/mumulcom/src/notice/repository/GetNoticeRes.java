package com.mumulcom.mumulcom.src.notice.repository;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetNoticeRes {
    private long questionIdx;
    private String noticeContent;
    private String diffTime;
    private String bigCategoryName;
    private int type;
    private int noticeCategoryIdx;
    private String title;
}
