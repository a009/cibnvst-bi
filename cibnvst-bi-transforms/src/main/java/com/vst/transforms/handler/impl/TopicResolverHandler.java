package com.vst.transforms.handler.impl;

import lombok.AllArgsConstructor;
import com.vst.transforms.handler.IResolverHandler;
import com.vst.transforms.searcher.ISearch;

/**
 * @author fucheng
 * @date 2022/10/1
 */
@AllArgsConstructor
public class TopicResolverHandler implements IResolverHandler {

    private final ISearch search;

    @Override
    public String getMapKey() {
        return "topicId";
    }

    @Override
    public ISearch getSearch() {
        return search;
    }
}
