package com.vst.etl.func;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.system.SystemUtil;

public class H2Functions {
    public static String formatPercent(Long x, Long y) {
        if (ObjectUtil.isNull(x) || NumberUtil.equals(x, 0)) return "0%";
        if (ObjectUtil.isNull(y) || NumberUtil.equals(y, 0)) return "100%";
        return NumberUtil.formatPercent((x * 1.0 - y) / y, 2);
    }

    public static String decimalFormat(Long x, Long y) {
        if (ObjectUtil.isNull(x) || NumberUtil.equals(x, 0)) return "0.00";
        if (ObjectUtil.isNull(y) || NumberUtil.equals(y, 0)) return "0.00";
        return NumberUtil.decimalFormat("#.00", x * 1.0 / y);
    }

    public static String randomString(int length) {
        return RandomUtil.randomString(length);
    }

    public static Long currentTimestamp() {
        return DateUtil.current();
    }

    public static String currentUser() {
        return SystemUtil.get("user.name", false);
    }
}
