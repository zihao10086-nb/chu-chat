package com.qiaochu.mallchat.common.common.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum YesOrNoEnum {
    No(0, "否"),
    Yes(1, "是");

    private final Integer status;
    private final String desc;
}
