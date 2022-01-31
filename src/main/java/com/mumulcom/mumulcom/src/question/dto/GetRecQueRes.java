package com.mumulcom.mumulcom.src.question.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor

public class GetRecQueRes {
    private long questionIdx;   //최근질문 순서, 가장최근 : 1
    private String BigCategory;
    private String SmallCategory;
    private String Name;
    private String Created;
    private String Title;
    private long Reply;
    private Long Like;
}
