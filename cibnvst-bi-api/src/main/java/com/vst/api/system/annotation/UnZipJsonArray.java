package com.vst.api.system.annotation;


import java.lang.annotation.*;

/**
 * @author fucheng
 * @date 2022/10/20
 * 解析Zip数据
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface UnZipJsonArray {
}
