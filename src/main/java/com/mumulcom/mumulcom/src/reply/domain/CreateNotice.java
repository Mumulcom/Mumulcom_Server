package com.mumulcom.mumulcom.src.reply.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class CreateNotice {

    private String noticeMessage;
    private Long userIdx;
    private List<Long> userIdxList;

}
