package com.vst.report.repository;

import com.vst.report.entity.UserEvents;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * @author fucheng
 * @date 2022/10/20
 */
public interface UserEventsRepository extends ElasticsearchRepository<UserEvents, String> {
    /**
     * 根据服务器识别IP查找用户
     * @param clientIP
     * @param pageable
     * @return
     */
    Page<UserEvents> findByClientIP(String clientIP, Pageable pageable);
}
