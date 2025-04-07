package com.dfn.lsf.repository.mapper;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.JdbcUtils;

/**
 * Default row mapper that maps a database row to Map<String, Object>
 * Used when no specific mapper is provided
 */
public class DefaultRowMapper implements RowMapper<Map<String, Object>> {
    
    @Override
    public Map<String, Object> mapRow(ResultSet rs, int rowNum) throws SQLException {
        ResultSetMetaData metaData = rs.getMetaData();
        int columnCount = metaData.getColumnCount();
        
        Map<String, Object> mapOfColumnValues = new HashMap<>(columnCount);
        
        for (int i = 1; i <= columnCount; i++) {
            String columnName = getColumnName(metaData, i);
            Object value = JdbcUtils.getResultSetValue(rs, i);
            
            // Handle empty strings
            if (value instanceof String && ((String) value).isEmpty()) {
                value = null;
            }
            
            mapOfColumnValues.put(columnName, value);
        }
        
        return mapOfColumnValues;
    }
    
    /**
     * Get the column name, using the label if available
     * 
     * @param metaData Result set metadata
     * @param columnIndex Column index
     * @return Column name
     * @throws SQLException If an error occurs
     */
    private String getColumnName(ResultSetMetaData metaData, int columnIndex) throws SQLException {
        String name = metaData.getColumnLabel(columnIndex);
        if (name == null || name.isEmpty()) {
            name = metaData.getColumnName(columnIndex);
        }
        return name;
    }
}