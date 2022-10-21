package com.vst.transforms.searcher;

import java.util.Map;

/**
 * @author fucheng
 * @date 2022/10/1
 */
public interface ISearch extends AutoCloseable {
    Map<String,Object> search(String key);
}
