package com.vst.api.listener;

import org.springframework.context.ApplicationEvent;

/**
 * @author fucheng
 * @date 2022/10/20
 */
public class UserEvent extends ApplicationEvent {
    public UserEvent(Object source) {
        super(source);
    }
}
