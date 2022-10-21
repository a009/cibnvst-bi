package com.vst.etl.repository;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.db.Entity;
import cn.hutool.db.StatementUtil;
import cn.hutool.db.sql.SqlBuilder;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.Map;

/**
 * @author fucheng
 * @date 2022/10/20
 */
public abstract class BaseRepository {

    public void insert(String tableName, List<Map<String, Object>> records) {
        if (!ObjectUtils.isEmpty(records)) {
            Map<String, Object> map = CollUtil.getFirst(records);

            Entity entity = Convert.convertQuietly(Entity.class, map);
            entity.setTableName(tableName);

            SqlBuilder sqlBuilder = SqlBuilder.create()
                    .insert(entity);

            int batchSize = 3000;
            getJdbcTemplate().batchUpdate(
                    sqlBuilder.build(),
                    records,
                    batchSize,
                    (ps, argument) -> StatementUtil.fillParams(ps, argument.values())
            );
        }
    }

    public List<Map<String, Object>> query(String sql, Object... args) {
        return getJdbcTemplate().queryForList(sql, args);
    }

    public void execute(String sql) {
        getJdbcTemplate().execute(sql);
    }

    public abstract JdbcTemplate getJdbcTemplate();
}
