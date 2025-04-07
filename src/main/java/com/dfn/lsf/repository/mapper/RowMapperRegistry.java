package com.dfn.lsf.repository.mapper;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.stereotype.Component;

import com.dfn.lsf.model.*;

/**
 * Registry for all row mappers
 * Provides centralized access to mappers for database result set mapping
 */
@Component
public class RowMapperRegistry implements MapperRegistry {
    
    private final Map<Class<?>, RowMapper<?>> typeMappers = new ConcurrentHashMap<>();
    private final Map<String, RowMapper<?>> namedMappers = new ConcurrentHashMap<>();
    private final DefaultRowMapper defaultRowMapper = new DefaultRowMapper();
    
    @Autowired
    public RowMapperRegistry(ApplicationContext applicationContext) {
        registerDefaultMappers();
    }
    
    /**
     * Get a row mapper for a specific Java type
     *
     * @param type Class to map to
     * @param <T> Type parameter
     * @return Row mapper for the type
     */
    @SuppressWarnings("unchecked")
    public <T> RowMapper<T> getMapper(Class<T> type) {
        return (RowMapper<T>) typeMappers.computeIfAbsent(type, this::createMapperForType);
    }
    
    /**
     * Get a row mapper by name
     *
     * @param name Mapper name
     * @return Row mapper
     */
    public RowMapper<?> getMapper(String name) {
        return namedMappers.getOrDefault(name, defaultRowMapper);
    }
    
    /**
     * Get a row mapper that maps to Map<String, Object>
     *
     * @return Map row mapper
     */
    public RowMapper<Map<String, Object>> getMapRowMapper() {
        return defaultRowMapper;
    }
    
    /**
     * Create a mapper for a type if not registered
     *
     * @param type Class to create mapper for
     * @return Row mapper
     */
    @SuppressWarnings("unchecked")
    private <T> RowMapper<T> createMapperForType(Class<T> type) {
        // Special handling for primitives
        if (type == String.class || type == Integer.class || type == Long.class || 
                type == Double.class || type == Boolean.class) {
            return (RowMapper<T>) new SingleColumnRowMapper<>(type);
        }
        
        // Default bean property mapper
        return new BeanPropertyRowMapper<>(type);
    }
    
    /**
     * Register all standard mappers
     */
    private void registerDefaultMappers() {
        // Register basic type mappers
        typeMappers.put(String.class, new SingleColumnRowMapper<>(String.class));
        typeMappers.put(Integer.class, new SingleColumnRowMapper<>(Integer.class));
        typeMappers.put(Long.class, new SingleColumnRowMapper<>(Long.class));
        typeMappers.put(Double.class, new SingleColumnRowMapper<>(Double.class));
        
        // Register domain model mappers
        registerModelMappers();
    }
    
    /**
     * Register domain model mappers
     */
    private void registerModelMappers() {
        // Register mappers for each domain model class 
        // If specialized mappers exist, use those; otherwise use BeanPropertyRowMapper
        
        // Example: typeMappers.put(PurchaseOrder.class, new PurchaseOrderMapper());
        // Default mappers for common model classes
        typeMappers.put(PurchaseOrder.class, new BeanPropertyRowMapper<>(PurchaseOrder.class));
        
        // Also register by name for backward compatibility
        namedMappers.put("purchaseOrder", typeMappers.get(PurchaseOrder.class));
    }
}