package com.dfn.lsf.repository.mapper;

import java.util.Map;

import org.springframework.jdbc.core.RowMapper;

/**
 * Interface for registry of database mappers
 * Centralizes access to row mappers for different entity types
 */
public interface MapperRegistry {
    
    /**
     * Get a row mapper for a specific Java type
     *
     * @param type Class to map to
     * @param <T> Type parameter
     * @return Row mapper for the type
     */
    <T> RowMapper<T> getMapper(Class<T> type);
    
    /**
     * Get a row mapper by name
     *
     * @param name Mapper name
     * @return Row mapper
     */
    RowMapper<?> getMapper(String name);
    
    /**
     * Get a row mapper that maps to Map<String, Object>
     *
     * @return Map row mapper
     */
    RowMapper<Map<String, Object>> getMapRowMapper();

    RowMapper<Map<String, String>> getMapRowMapperString();
}