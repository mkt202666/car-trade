package com.pancosky.cartradeadmin.handler;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 通用 JSONB 字符串 TypeHandler
 *
 * 用途：实体字段类型为 String，对应数据库列为 JSONB / JSON 时，
 *       通过此 TypeHandler 在写入时使用 Types.OTHER（PostgreSQL jsonb_in），
 *       读取时按字符串读出。
 *
 * 使用方法：
 *   1) application.properties 增加
 *      mybatis-plus.type-handlers-package=com.pancosky.cartradeadmin.handler
 *   2) 实体字段加：
 *      @TableField(typeHandler = JsonbStringTypeHandler.class)
 *      private String notificationSettings;
 */
@MappedTypes(String.class)
@MappedJdbcTypes(JdbcType.OTHER)
public class JsonbStringTypeHandler extends BaseTypeHandler<String> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, String parameter, JdbcType jdbcType) throws SQLException {
        // Types.OTHER + 字符串值 → PG 驱动按 jsonb_in 处理
        ps.setObject(i, parameter, java.sql.Types.OTHER);
    }

    @Override
    public String getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return rs.getString(columnName);
    }

    @Override
    public String getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return rs.getString(columnIndex);
    }

    @Override
    public String getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return cs.getString(columnIndex);
    }
}
