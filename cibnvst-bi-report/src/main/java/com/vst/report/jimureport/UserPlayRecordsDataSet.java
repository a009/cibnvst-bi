package com.vst.report.jimureport;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.RegexPool;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import com.vst.report.entity.UserEvents;
import lombok.AllArgsConstructor;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.jeecg.modules.jmreport.api.data.IDataSetFactory;
import org.jeecg.modules.jmreport.desreport.model.JmPage;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * @author fucheng
 * @date 2022/10/20
 */
@Component
@AllArgsConstructor
public class UserPlayRecordsDataSet implements IDataSetFactory {

    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    @Override
    public List<Map<String, Object>> createData(Map<String, Object> map) {
        return null;
    }

    @Override
    public JmPage<Map<String, Object>> createPageData(Map<String, Object> map) {
        Integer pageNo = MapUtil.getInt(map, "pageNo", 1);
        Integer pageSize = MapUtil.getInt(map, "pageSize", 20);
        String clientIP = getClientIPElseDefaultLocal(map);

        TermQueryBuilder termQueryBuilder = new TermQueryBuilder("clientIP", clientIP);
        NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder();
        nativeSearchQueryBuilder.withQuery(termQueryBuilder);
        nativeSearchQueryBuilder.withSort(Sort.by("requestTime.keyword").descending());
        nativeSearchQueryBuilder.withPageable(PageRequest.of(pageNo - 1, pageSize));
        SearchHits<UserEvents> search = elasticsearchRestTemplate.search(nativeSearchQueryBuilder.build(), UserEvents.class);

        List<Map<String, Object>> records = search
                .stream()
                .map(SearchHit::getContent)
                .map(BeanUtil::beanToMap)
                .toList();

        JmPage<Map<String, Object>> jmPage = new JmPage<>();
        jmPage.setPageNo(pageNo);
        jmPage.setPageSize(pageSize);
        jmPage.setTotal((int) search.getTotalHits());
        jmPage.setRecords(records);
        return jmPage;
    }

    private String getClientIPElseDefaultLocal(Map<String,Object>  map){
        String clientIP = MapUtil.getStr(map, "clientIP", "113.87.131.246");

        if (StrUtil.isBlank(clientIP)){
            RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
            clientIP = (String) requestAttributes.getAttribute("clientIP", RequestAttributes.SCOPE_REQUEST);
        }

        if (!Pattern.matches(RegexPool.IPV4, clientIP)){
            throw new IllegalArgumentException("无效的IP");
        }

        return clientIP;
    }
}
