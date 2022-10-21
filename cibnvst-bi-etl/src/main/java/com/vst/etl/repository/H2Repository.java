package com.vst.etl.repository;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.dynamic.datasource.annotation.DS;
import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author fucheng
 * @date 2022/10/20
 */
@DS("h2")
@Repository
@AllArgsConstructor
public class H2Repository extends BaseRepository{
    private final JdbcTemplate jdbcTemplate;

    @Override
    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }

    private static final String CREATE_TABLE_SQL = "CREATE TABLE IF NOT EXISTS {tableName}({columnInfo})";
    private static final String DROP_TABLE_SQL = "DROP TABLE IF EXISTS {tableName}";

    public void createTable(String tableName, Map<String, String> columnMap) {
        String columnInfo = MapUtil.join(columnMap, ",", " ");
        Map<Object, Object> parameter = MapUtil.builder()
                .put("tableName", tableName)
                .put("columnInfo", columnInfo)
                .build();
        String sql = StrUtil.format(CREATE_TABLE_SQL, parameter);
        super.execute(sql);
    }

    public void dropTable(String tableName) {
        super.execute(StrUtil.format(DROP_TABLE_SQL, Collections.singletonMap("tableName", tableName)));
    }

    public List<Map<String, Object>> query(String sql) {
        return super.query(sql);
    }
}
