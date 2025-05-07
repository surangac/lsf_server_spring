package com.dfn.lsf.repository.oracle;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import com.dfn.lsf.repository.mapper.RowMapperFactory;

/**
 * Modern Java 21 implementation of Oracle database operations
 * Replaces the original OracleDaoImpl
 */
@Repository
public class OracleJdbcRepository implements OracleRepository {
    
    private static final Logger logger = LoggerFactory.getLogger(OracleJdbcRepository.class);
    
    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final RowMapperFactory rowMapperFactory;
    private final ExecutorService executorService;
    
    @Value("${spring.datasource.schema:}")
    private String schema;    

    public OracleJdbcRepository(DataSource dataSource, RowMapperFactory rowMapperFactory) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        this.rowMapperFactory = rowMapperFactory;
        // Create a virtual thread per task executor for Java 21
        this.executorService = Executors.newVirtualThreadPerTaskExecutor();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> List<T> query(String query, Map<String, ?> params, String mapperType) {
        try {
            MapSqlParameterSource paramSource = createParameterSource(params);
            RowMapper<?> rowMapper = rowMapperFactory.getRowMapper(mapperType);
            
            return (List<T>) namedParameterJdbcTemplate.query(
                    query, 
                    paramSource, 
                    rowMapper
            );
        } catch (DataAccessException e) {
            logger.error("Error executing query: {}", query, e);
            return Collections.emptyList();
        }
    }

    @Override
    public int update(String query, Map<String, ?> params) {
        try {
            MapSqlParameterSource paramSource = createParameterSource(params);
            return namedParameterJdbcTemplate.update(query, paramSource);
        } catch (DataAccessException e) {
            logger.error("Error executing update: {}", query, e);
            return 0;
        }
    }

    @Override
    public String executeProc(String packageName, String procedureName, Map<String, ?> params) {
        try {
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withSchemaName(schema)
                    .withCatalogName(packageName)
                    .withProcedureName(procedureName);
            
            SqlParameterSource paramSource = new MapSqlParameterSource(params);
            Map<String, Object> result = jdbcCall.execute(paramSource);
            
            return Optional.ofNullable(result.get("PKEY"))
                    .map(Object::toString)
                    .orElse(null);
        } catch (Exception e) {
            logger.error("Error executing procedure {}.{}", packageName, procedureName, e);
            return null;
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> List<T> getProcResult(String packageName, String procedureName, Map<String, ?> params, RowMapper<T> rowMapper) {
        try {
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withSchemaName(schema)
                    .withCatalogName(packageName)
                    .withProcedureName(procedureName)
                    .returningResultSet("pview", rowMapper);
            
            SqlParameterSource paramSource = new MapSqlParameterSource(params);
            Map<String, Object> result = jdbcCall.execute(paramSource);
            
            return (List<T>) result.get("pview");
        } catch (Exception e) {
            logger.error("Error executing procedure {}.{}", 
                    packageName, procedureName, e);
            return Collections.emptyList();
        }
    }

    @Override
    public List<Map<String, Object>> getProcResult(String packageName, String procedureName, Map<String, ?> params) {
        try {
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withSchemaName(schema)
                    .withCatalogName(packageName)
                    .withProcedureName(procedureName);
            
            SqlParameterSource paramSource = new MapSqlParameterSource(params);
            Map<String, Object> result = jdbcCall.execute(paramSource);
            
            if (!result.isEmpty()) {
                // Try uppercase key first (Oracle often returns uppercase)
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> resultList = (List<Map<String, Object>>) result.get("PVIEW");
                
                // If null, try lowercase key
                if (resultList == null) {
                    @SuppressWarnings("unchecked")
                    List<Map<String, Object>> lowercaseResult = (List<Map<String, Object>>) result.get("pview");
                    resultList = lowercaseResult;
                }
                
                // If both are null, look for any other list in the result
                if (resultList == null) {
                    for (String key : result.keySet()) {
                        Object value = result.get(key);
                        if (value instanceof List) {
                            @SuppressWarnings("unchecked")
                            List<Map<String, Object>> listValue = (List<Map<String, Object>>) value;
                            resultList = listValue;
                            logger.info("Found result list with key: {}", key);
                            break;
                        }
                    }
                }
                
                return resultList != null ? resultList : Collections.emptyList();
            } else {
                return Collections.emptyList();
            }
        } catch (Exception e) {
            logger.error("Error executing procedure {}.{}", packageName, procedureName, e);
            return Collections.emptyList();
        }
    }
    
    /**
     * Execute a query asynchronously using virtual threads (Java 21 feature)
     *
     * @param query SQL query with named parameters
     * @param params Map of parameter names and values
     * @param mapperType Row mapper identifier
     * @return CompletableFuture of mapped objects
     * @param <T> Type of objects returned
     */
    @SuppressWarnings("unchecked")
    public <T> CompletableFuture<List<T>> queryAsync(String query, Map<String, ?> params, String mapperType) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                MapSqlParameterSource paramSource = createParameterSource(params);
                RowMapper<?> rowMapper = rowMapperFactory.getRowMapper(mapperType);
                
                return (List<T>) namedParameterJdbcTemplate.query(
                        query, 
                        paramSource, 
                        rowMapper
                );
            } catch (DataAccessException e) {
                logger.error("Error executing async query: {}", query, e);
                return Collections.emptyList();
            }
        }, executorService);
    }
    
    /**
     * Execute a stored procedure asynchronously using virtual threads (Java 21 feature)
     *
     * @param packageName Oracle package name
     * @param procedureName Procedure name
     * @param params Procedure parameters
     * @param mapperType Row mapper identifier
     * @return CompletableFuture of mapped objects
     * @param <T> Type of objects returned
     */
    @SuppressWarnings("unchecked")
    public <T> CompletableFuture<List<T>> getProcResultAsync(
            String packageName, String procedureName, Map<String, ?> params, String mapperType) {
        
        return CompletableFuture.supplyAsync(() -> {
            try {
                RowMapper<?> rowMapper = rowMapperFactory.getRowMapper(mapperType);
                
                SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                        .withSchemaName(schema)
                        .withCatalogName(packageName)
                        .withProcedureName(procedureName)
                        .returningResultSet("pview", rowMapper);
                
                SqlParameterSource paramSource = new MapSqlParameterSource(params);
                Map<String, Object> result = jdbcCall.execute(paramSource);
                
                return (List<T>) result.get("pview");
            } catch (Exception e) {
                logger.error("Error executing async procedure {}.{} with mapper: {}", 
                        packageName, procedureName, mapperType, e);
                return Collections.emptyList();
            }
        }, executorService);
    }
    
    /**
     * Create a parameter source from a map, handling null parameters
     * 
     * @param params Map of parameters or null
     * @return MapSqlParameterSource
     */
    private MapSqlParameterSource createParameterSource(Map<String, ?> params) {
        MapSqlParameterSource paramSource = new MapSqlParameterSource();
        if (params != null) {
            paramSource.addValues(params);
        }
        return paramSource;
    }
}