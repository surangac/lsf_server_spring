package com.dfn.lsf.repository.oracle;

import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.RowMapper;

/**
 * Interface for Oracle database operations
 * Modern Java 21 equivalent of the original OracleDaoI
 */
public interface OracleRepository {

    /**
     * Execute a query and map results using the specified row mapper
     *
     * @param query SQL query with named parameters
     * @param params Map of parameter names and values
     * @param mapperType Row mapper identifier
     * @return List of mapped objects
     * @param <T> Type of objects returned
     */
    <T> List<T> query(String query, Map<String, ?> params, String mapperType);

    /**
     * Execute an update query
     *
     * @param query SQL query with named parameters
     * @param params Map of parameter names and values
     * @return Number of rows affected
     */
    int update(String query, Map<String, ?> params);

    /**
     * Execute a stored procedure that returns a string key
     *
     * @param packageName Oracle package name
     * @param procedureName Procedure name
     * @param params Procedure parameters
     * @return Result key from the procedure
     */
    String executeProc(String packageName, String procedureName, Map<String, ?> params);

    /**
     * Execute a stored procedure that returns a cursor
     *
     * @param packageName Oracle package name
     * @param procedureName Procedure name
     * @param params Procedure parameters
     * @param rowMapper Row mapper to convert rows to objects
     * @return List of mapped objects from cursor
     * @param <T> Type of objects returned
     */
    <T> List<T> getProcResult(String packageName, String procedureName, Map<String, ?> params, RowMapper<T> rowMapper);

    /**
     * Execute a stored procedure that returns a cursor
     * Uses default mapper
     *
     * @param packageName Oracle package name
     * @param procedureName Procedure name
     * @param params Procedure parameters
     * @return List of mapped objects from cursor
     */
    List<Map<String, Object>> getProcResult(String packageName, String procedureName, Map<String, ?> params);
}