package com.vst.api.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author fucheng
 * @date 2022/10/24
 */
@Slf4j
@RestControllerAdvice
public class GlobalException {

    /**
     * 参数异常
     * @param e
     * @return
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public String argumentError(IllegalArgumentException e){
        log.error("IllegalArgumentException {}", e.getMessage());
        return "error";
    }

    /**
     * 最终异常
     * @param e
     * @return
     */
    @ExceptionHandler(Exception.class)
    public String globalError(Exception e){
        log.error("Exception {}", e.getMessage());
        return "error";
    }
}
