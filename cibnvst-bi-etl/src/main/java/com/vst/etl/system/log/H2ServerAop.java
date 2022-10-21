package com.vst.etl.system.log;

import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * @author fucheng
 * @date 2022/10/9
 */
@Aspect
@Slf4j
@Component
public class H2ServerAop {
    @Pointcut("execution(public * com.vst.etl.repository.H2Repository.*(..)) || execution(public * com.vst.etl.repository.DorisRepository.*(..))")
    public void pointcut(){

    }

    @Around("pointcut()")
    public Object around(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        Signature signature = proceedingJoinPoint.getSignature();
        Method method = ((MethodSignature) signature).getMethod();
        Object[] args = proceedingJoinPoint.getArgs();
        log.debug(">>> method: {} args: {}", method.getName(), JSONUtil.toJsonStr(args));
        Object ret = proceedingJoinPoint.proceed(args);
        log.debug(">>> method end");
        return ret;
    }
}
