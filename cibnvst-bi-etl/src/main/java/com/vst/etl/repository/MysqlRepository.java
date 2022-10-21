package com.vst.etl.repository;

import cn.hutool.db.sql.Condition;
import cn.hutool.db.sql.SqlBuilder;
import com.baomidou.dynamic.datasource.annotation.DS;
import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.Map;

/**
 * @author fucheng
 * @date 2022/10/20
 */
@DS("mysql")
@Repository
@AllArgsConstructor
public class MysqlRepository extends BaseRepository{
    private final JdbcTemplate jdbcTemplate;

    @Override
    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }

    private final String QUERY_TABLE_SCHEMA = "vst_bi";
    private final String TABLE_NAME = "information_schema.COLUMNS";

    public List<Map<String, Object>> scanSchema(String tableName, List<String> columns) {
        Condition[] conditions = {
                new Condition("TABLE_SCHEMA", QUERY_TABLE_SCHEMA),
                new Condition("TABLE_NAME", tableName),
                new Condition("COLUMN_NAME", columns)
        };

        String[] select = new String[]{"COLUMN_NAME", "DATA_TYPE"};

        SqlBuilder sqlBuilder = SqlBuilder.create()
                .select(select)
                .from(TABLE_NAME)
                .where(conditions);

        return super.query(sqlBuilder.build(), sqlBuilder.getParamValueArray());
    }

    @Transactional
    public void deleteInsert(String tableName, String deleteColumn, String deleteValue, List<Map<String, Object>> insertRecords) {
        if (!ObjectUtils.isEmpty(insertRecords)) {
            delete(tableName, deleteColumn, deleteValue);
            insert(tableName, insertRecords);
        }
    }

    public void delete(String tableName, String column, Object value) {
        Condition condition = new Condition(column, value);
        condition.setPlaceHolder(false);
        SqlBuilder sqlBuilder = SqlBuilder.create()
                .delete(tableName)
                .where(condition);
        super.execute(sqlBuilder.build());
    }

    public List<Map<String, Object>> query(String tableName, String queryColumn, Object queryValue, List<String> select) {
        SqlBuilder sqlBuilder = SqlBuilder.create()
                .select(select)
                .from(tableName)
                .where(new Condition(queryColumn, queryValue));
        return super.query(sqlBuilder.build(), sqlBuilder.getParamValueArray());
    }
}
