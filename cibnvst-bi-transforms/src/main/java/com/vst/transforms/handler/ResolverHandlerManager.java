package com.vst.transforms.handler;

import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author fucheng
 * @date 2022/10/1
 */
@AllArgsConstructor
public class ResolverHandlerManager implements AutoCloseable {
    private final List<IResolverHandler> handlerList;

    public void resolver(Map<String, Object> map){
        for (IResolverHandler handler : handlerList) {
            handler.resolver(map);
        }
    }

    public static ResolverHandlerManager create(){
        return new ResolverHandlerManager(new ArrayList<>());
    }

    public ResolverHandlerManager addResolverHandler(IResolverHandler resolverHandler){
        handlerList.add(resolverHandler);
        return this;
    }

    @Override
    public void close() throws Exception {
        for (IResolverHandler resolverHandler : handlerList) {
            resolverHandler.close();
        }
    }
}
