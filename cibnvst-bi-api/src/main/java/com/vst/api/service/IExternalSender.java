package com.vst.api.service;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * @author fucheng
 * @date 2022/10/24
 */
public interface IExternalSender {
    void send(JsonNode jsonNode);
}
