package com.qiaochu.mallchat.common.user.domain.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 修改用户名请求参数
 */
@Data
public class WearingBadgeReq {
    @ApiModelProperty("徽章id")
    @NotNull
    private Long itemId;
}
