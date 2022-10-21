package com.vst.api.controller;

import cn.hutool.json.JSONObject;
import com.vst.api.entity.UserEventHeader;
import com.vst.api.listener.UserEvent;
import com.vst.api.system.annotation.UnZipJsonArray;
import com.vst.api.system.holder.UserEventHeaderContextHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author fucheng
 * @date 2022/10/20
 */
@RestController
public class ApiController {

    @Autowired
    ApplicationContext applicationContext;

    /**
     * 接收所有埋点数据
     *
     * @return
     */
    @RequestMapping("/**")
    public String receive(@UnZipJsonArray List<JSONObject> jsonStr) {
        jsonStr.stream()
                .map(j -> {
                    UserEventHeader userEventHeader = UserEventHeaderContextHolder.currentRequestHeaderWrap();
                    userEventHeader.setAttrs(j);
                    return userEventHeader;
                }).map(UserEvent::new)
                .forEach(applicationContext::publishEvent);
        return "ok";
    }
}
