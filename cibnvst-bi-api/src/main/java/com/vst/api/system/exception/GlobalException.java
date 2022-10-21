package com.vst.api.system.exception;

import cn.hutool.json.JSONException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author fucheng
 * @date 2022/10/20
 */
@RestControllerAdvice
@Slf4j
public class GlobalException {

    @ExceptionHandler(JSONException.class)
    public String jsonException(JSONException e){
        log.error("Intercepting json Exceptions: {}", e.getMessage());
        return "error";
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public String argumentException(IllegalArgumentException e){
        log.error("Abnormal interception parameters: {}", e.getMessage());
        return "error";
    }

    @ExceptionHandler(Exception.class)
    public String exception(Exception e){
        log.error("A major exception was intercepted: ", e);
        return "error";
    }
}
