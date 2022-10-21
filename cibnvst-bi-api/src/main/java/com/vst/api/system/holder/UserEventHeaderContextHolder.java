package com.vst.api.system.holder;

import cn.hutool.core.date.DateUtil;
import cn.hutool.extra.servlet.ServletUtil;
import com.vst.api.entity.UserEventHeader;
import org.springframework.http.HttpHeaders;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * @author fucheng
 * @date 2022/10/20
 */
public class UserEventHeaderContextHolder {

    public static UserEventHeader currentRequestHeaderWrap() {
        ServletRequestAttributes servletRequestAttributes =
                (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = servletRequestAttributes.getRequest();

        UserEventHeader userEventHeader = new UserEventHeader();
        userEventHeader.setClientIP(ServletUtil.getClientIP(request));
        userEventHeader.setContentType(request.getContentType());
        userEventHeader.setRequestURI(request.getRequestURI());
        userEventHeader.setRequestURL(request.getRequestURL().toString());
        userEventHeader.setServerPort(request.getServerPort());
        userEventHeader.setServerName(request.getServerName());
        userEventHeader.setMethod(request.getMethod());
        userEventHeader.setUserAgent(request.getHeader(HttpHeaders.USER_AGENT));
        userEventHeader.setRequestTime(DateUtil.current());
        return userEventHeader;
    }
}
