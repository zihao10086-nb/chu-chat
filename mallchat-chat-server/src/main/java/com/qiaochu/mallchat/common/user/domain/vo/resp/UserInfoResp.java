package com.qiaochu.mallchat.common.user.domain.vo.resp;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class UserInfoResp {
    @ApiModelProperty(value = "uid")
    private Long id;
    @ApiModelProperty(value = "用户头像")
    private String avatar;
    @ApiModelProperty(value = "用户名")
    private String name;
    @ApiModelProperty(value = "性别")
    private String sex;
    @ApiModelProperty(value = "改名次数")
    private Integer modifyNameChance;
}
