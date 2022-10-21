package com.vst.etl.repository;

import com.baomidou.dynamic.datasource.annotation.DS;
import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @author fucheng
 * @date 2022/10/20
 */
@DS("doris")
@Repository
@AllArgsConstructor
public class DorisRepository extends BaseRepository{

    private final JdbcTemplate jdbcTemplate;

    public List<Map<String, Object>> query(String sql) {
        return super.query(sql);
    }

    public void execute(String sql) {
        super.execute(sql);
    }

    @Override
    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }
}
