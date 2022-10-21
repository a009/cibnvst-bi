package com.vst.transforms.searcher.impl;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import com.vst.transforms.searcher.ISearch;
import org.lionsoul.ip2region.xdb.Searcher;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class IPSearcher implements AutoCloseable, ISearch {
    private final Searcher searcher;

    public IPSearcher(String dbPath) throws IOException {
        searcher = Searcher.newWithFileOnly(dbPath);
    }

    public Map<String, Object> search(String ip) {
        try {
            //中国|0|广东省|深圳市|电信
            String search = searcher.search(ip);
            String[] address = StrUtil.splitToArray(search, "|");

            Map<String, Object> addressMap = new HashMap<>();
            if (address.length > 0) {
                addressMap.put("country", address[0]);
            }
            if (address.length > 2) {
                addressMap.put("province", address[2]);
            }
            if (address.length > 3) {
                addressMap.put("city", address[3]);
            }
            return addressMap;
        } catch (Exception e) {
            return MapUtil.empty();
        }
    }

    @Override
    public void close() throws Exception {
        searcher.close();
    }
}