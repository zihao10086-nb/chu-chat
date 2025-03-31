package com.qiaochu.mallchat.common.user.domain.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;

@Data
public class IpInfo implements Serializable {
    //注册时ip
    private String createIp;
    //注册时ip详细信息
    private IpDetail createIpDetail;
    //最新登录ip
    private String updateIp;
    //最新登录ip详细信息
    private IpDetail updateIpDetail;

    public void refreshIp(String ip) {
        if (createIp == null){
            createIp = ip;
        }
        updateIp = ip;
    }

    public String needRefresh() {
        boolean notNeedRefresh = Optional.ofNullable(updateIpDetail)
                .map(IpDetail::getIp)
                .filter(ip -> Objects.equals(ip, updateIp))
                .isPresent();
        return notNeedRefresh?null:updateIp;
    }

    public void refreshIpDetail(IpDetail ipDetail) {
        if (Objects.equals(createIp, ipDetail.getIp())){
            createIpDetail = ipDetail;
        }
        if (Objects.equals(updateIp, ipDetail.getIp())){
            updateIpDetail = ipDetail;
        }
    }
}
