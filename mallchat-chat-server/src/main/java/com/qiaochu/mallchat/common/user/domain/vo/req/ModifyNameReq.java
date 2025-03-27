package com.qiaochu.mallchat.common.user.domain.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

/**
 * 修改用户名请求参数
 */
@Data
public class ModifyNameReq {
    @ApiModelProperty("用户名")
    @NotBlank
    @Length(max = 6,message = "用户名不可以取太长，不然我们记不住哦")
    private String name;
}
