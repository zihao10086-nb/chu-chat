package com.qiaochu.mallchat.common.user.controller;


import com.qiaochu.mallchat.common.common.domain.dto.RequestInfo;
import com.qiaochu.mallchat.common.common.domain.vo.resp.ApiResult;
import com.qiaochu.mallchat.common.common.utils.RequestHolder;
import com.qiaochu.mallchat.common.user.domain.vo.req.ModifyNameReq;
import com.qiaochu.mallchat.common.user.domain.vo.req.WearingBadgeReq;
import com.qiaochu.mallchat.common.user.domain.vo.resp.BadgeResp;
import com.qiaochu.mallchat.common.user.domain.vo.resp.UserInfoResp;
import com.qiaochu.mallchat.common.user.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import org.springframework.stereotype.Controller;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

/**
 * <p>
 * 用户表 前端控制器
 * </p>
 *
 * @author ${author}
 * @since 2025-03-24
 */
@RestController
@RequestMapping("/capi/user")
@Api(tags = "用户接口模块")
public class UserController {

    @Resource
    private UserService userService;

    @GetMapping("/userInfo")
    @ApiOperation("根据uid获取用户信息")
    public ApiResult<UserInfoResp> getUserInfo() {
        return ApiResult.success(userService.getUserInfo(RequestHolder.get().getUid()));
    }

    @PutMapping("/name")
    @ApiOperation("修改用户名")
    public ApiResult<UserInfoResp> modifyName(@Valid @RequestBody ModifyNameReq req) {
        userService.modifyName(RequestHolder.get().getUid(),req.getName());
        return ApiResult.success();
    }
    @GetMapping("/badges")
    @ApiOperation("可选徽章预览")
    public ApiResult<List<BadgeResp>> badges() {
        return ApiResult.success(userService.badges(RequestHolder.get().getUid()));
    }
    @PutMapping("/badge")
    @ApiOperation("佩戴徽章")
    public ApiResult<Void> wearingBadge(@Valid @RequestBody WearingBadgeReq req) {
        userService.wearingBadge(RequestHolder.get().getUid(),req.getItemId());
        return ApiResult.success();
    }
}

