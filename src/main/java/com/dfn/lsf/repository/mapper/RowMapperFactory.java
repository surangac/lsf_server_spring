package com.dfn.lsf.repository.mapper;

import com.dfn.lsf.model.*;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Factory for creating row mappers
 * Modern Java 21 implementation with functional interfaces
 */
@Component
public class RowMapperFactory {

    private final Map<String, RowMapper<?>> mappers = new ConcurrentHashMap<>();
    
    /**
     * Initialize factory with all available mappers
     */
    public RowMapperFactory() {
        registerDefaultMappers();
    }
    
    /**
     * Get a row mapper by type
     *
     * @param type Mapper type identifier
     * @return RowMapper for the specified type
     */
    public RowMapper<?> getRowMapper(String type) {
        return mappers.computeIfAbsent(type, this::createMapper);
    }
    
    /**
     * Register a new mapper
     *
     * @param type Mapper type identifier
     * @param mapper RowMapper instance
     */
    public void registerMapper(String type, RowMapper<?> mapper) {
        mappers.put(type, mapper);
    }
    
    /**
     * Create a mapper if not found
     * This is a fallback method that attempts to create mappers dynamically
     *
     * @param type Mapper type identifier
     * @return RowMapper or DefaultRowMapper if type unknown
     */
    private RowMapper<?> createMapper(String type) {
        try {
            // Try to dynamically instantiate a mapper class if it exists
            // This assumes a naming convention of [Type]Mapper in the same package
            String mapperClassName = "com.dfn.lsf.repository.mapper." + type + "Mapper";
            Class<?> mapperClass = Class.forName(mapperClassName);
            return (RowMapper<?>) mapperClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            // If mapper not found, return a default mapper that maps to generic Map
            return new DefaultRowMapper();
        }
    }
    
    /**
     * Register all default mappers
     * This method registers all standard mappers used in the application
     */
    private void registerDefaultMappers() {
        // Register common mappers
        // These mappers would be defined as separate classes in the repository.mapper package
        // For example: PurchaseOrderMapper, ApplicationMapper, etc.
        
        // This is just a placeholder - actual implementation would register real mappers
        mappers.put("default", new DefaultRowMapper());
        
        // Example: Register other mappers as they're implemented
        // mappers.put("purchaseOrder", new PurchaseOrderMapper());
        // mappers.put("application", new ApplicationMapper());
        // mappers.put("user", new UserMapper());
    }
    
    /**
     * Default row mapper that maps to Map<String, Object>
     */
    private static class DefaultRowMapper implements RowMapper<Map<String, Object>> {
        @Override
        public Map<String, Object> mapRow(java.sql.ResultSet rs, int rowNum) throws java.sql.SQLException {
            Map<String, Object> result = new HashMap<>();
            var metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();
            
            for (int i = 1; i <= columnCount; i++) {
                String columnName = metaData.getColumnName(i);
                Object value = rs.getObject(i);
                result.put(columnName, value);
            }
            
            return result;
        }
    }
}