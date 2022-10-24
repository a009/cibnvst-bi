package com.vst.api.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.vst.api.common.UnZipToJsonNode;
import com.vst.api.service.IExternalSender;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author fucheng
 * @date 2022/10/20
 */
@RestController
@AllArgsConstructor
public class ApiController {
    private IExternalSender externalSender;

    /**
     * 接收埋点数据
     */
    @PostMapping({"/action/**", "/logs", "/userdata/**"})
    public String receive(@UnZipToJsonNode JsonNode jsonNode) {
        externalSender.send(jsonNode);
        return "ok";
    }
}
