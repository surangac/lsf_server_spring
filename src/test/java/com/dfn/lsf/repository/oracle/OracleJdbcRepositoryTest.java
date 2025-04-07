package com.dfn.lsf.repository.oracle;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import javax.sql.DataSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.test.util.ReflectionTestUtils;

import com.dfn.lsf.repository.mapper.RowMapperFactory;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings({"unchecked", "rawtypes"})
public class OracleJdbcRepositoryTest {

    @Mock
    private DataSource dataSource;

    @Mock
    private JdbcTemplate jdbcTemplate;

    @Mock
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Mock
    private RowMapperFactory rowMapperFactory;

    @Mock
    private RowMapper rowMapper;

    @Mock
    private SimpleJdbcCall simpleJdbcCall;

    private OracleJdbcRepository repository;

    @BeforeEach
    public void setup() {
        repository = new OracleJdbcRepository(dataSource, rowMapperFactory);
        ReflectionTestUtils.setField(repository, "jdbcTemplate", jdbcTemplate);
        ReflectionTestUtils.setField(repository, "namedParameterJdbcTemplate", namedParameterJdbcTemplate);
        ReflectionTestUtils.setField(repository, "schema", "TEST_SCHEMA");
    }

    @Test
    public void testQuery() {
        // Arrange
        String query = "SELECT * FROM TEST_TABLE WHERE ID = :id";
        Map<String, Object> params = Map.of("id", 1);
        List<Object> expectedResults = List.of(Map.of("ID", 1, "NAME", "Test"));
        
        when(rowMapperFactory.getRowMapper("testMapper")).thenReturn(rowMapper);
        when(namedParameterJdbcTemplate.query(eq(query), any(MapSqlParameterSource.class), eq(rowMapper))).thenReturn(expectedResults);

        // Act
        List<Object> results = repository.query(query, params, "testMapper");

        // Assert
        assertEquals(expectedResults, results);
        verify(namedParameterJdbcTemplate).query(eq(query), any(MapSqlParameterSource.class), eq(rowMapper));
    }

    @Test
    public void testUpdate() {
        // Arrange
        String query = "UPDATE TEST_TABLE SET NAME = :name WHERE ID = :id";
        Map<String, Object> params = Map.of("id", 1, "name", "Updated");
        when(namedParameterJdbcTemplate.update(eq(query), any(MapSqlParameterSource.class))).thenReturn(1);

        // Act
        int result = repository.update(query, params);

        // Assert
        assertEquals(1, result);
        verify(namedParameterJdbcTemplate).update(eq(query), any(MapSqlParameterSource.class));
    }

    @Test
    @Disabled("Issues with static mocking for SimpleJdbcCall")
    public void testExecuteProc() {
        // Arrange
        String packageName = "TEST_PKG";
        String procedureName = "TEST_PROC";
        Map<String, Object> params = Map.of("param1", "value1");
        Map<String, Object> results = Map.of("PKEY", "123");
        
        when(jdbcTemplate.getDataSource()).thenReturn(dataSource);
        when(simpleJdbcCall.withSchemaName(anyString())).thenReturn(simpleJdbcCall);
        when(simpleJdbcCall.withCatalogName(anyString())).thenReturn(simpleJdbcCall);
        when(simpleJdbcCall.withProcedureName(anyString())).thenReturn(simpleJdbcCall);
        when(simpleJdbcCall.execute(any(MapSqlParameterSource.class))).thenReturn(results);
        
        // Create a static mock for SimpleJdbcCall constructor
        try (MockedStatic<SimpleJdbcCall> mockedStatic = Mockito.mockStatic(SimpleJdbcCall.class)) {
            // Configure the static mock
            mockedStatic.when(() -> new SimpleJdbcCall(jdbcTemplate)).thenReturn(simpleJdbcCall);
            
            // Act
            String result = repository.executeProc(packageName, procedureName, params);
            
            // Assert
            assertEquals("123", result);
            verify(simpleJdbcCall).withSchemaName("TEST_SCHEMA");
            verify(simpleJdbcCall).withCatalogName(packageName);
            verify(simpleJdbcCall).withProcedureName(procedureName);
        }
    }

    @Test
    @Disabled("Issues with static mocking for SimpleJdbcCall")
    public void testGetProcResultWithMapper() {
        // Arrange
        String packageName = "TEST_PKG";
        String procedureName = "TEST_PROC";
        Map<String, Object> params = Map.of("param1", "value1");
        List<Object> expectedResults = List.of(Map.of("ID", 1, "NAME", "Test"));
        Map<String, Object> results = Map.of("CURSOR", expectedResults);
        
        when(jdbcTemplate.getDataSource()).thenReturn(dataSource);
        when(simpleJdbcCall.withSchemaName(anyString())).thenReturn(simpleJdbcCall);
        when(simpleJdbcCall.withCatalogName(anyString())).thenReturn(simpleJdbcCall);
        when(simpleJdbcCall.withProcedureName(anyString())).thenReturn(simpleJdbcCall);
        when(simpleJdbcCall.returningResultSet(anyString(), eq(rowMapper))).thenReturn(simpleJdbcCall);
        when(simpleJdbcCall.execute(any(MapSqlParameterSource.class))).thenReturn(results);
        
        // Create a static mock for SimpleJdbcCall constructor
        try (MockedStatic<SimpleJdbcCall> mockedStatic = Mockito.mockStatic(SimpleJdbcCall.class)) {
            // Configure the static mock
            mockedStatic.when(() -> new SimpleJdbcCall(jdbcTemplate)).thenReturn(simpleJdbcCall);
            
            // Act
            List<Object> result = repository.getProcResult(packageName, procedureName, params, rowMapper);
            
            // Assert
            assertEquals(expectedResults, result);
            verify(simpleJdbcCall).withSchemaName("TEST_SCHEMA");
            verify(simpleJdbcCall).withCatalogName(packageName);
            verify(simpleJdbcCall).withProcedureName(procedureName);
            verify(simpleJdbcCall).returningResultSet(eq("CURSOR"), eq(rowMapper));
        }
    }

    @Test
    @Disabled("Issues with static mocking for SimpleJdbcCall")
    public void testGetProcResultWithoutMapper() {
        // Arrange
        String packageName = "TEST_PKG";
        String procedureName = "TEST_PROC";
        Map<String, Object> params = Map.of("param1", "value1");
        List<Map<String, Object>> expectedResults = List.of(Map.of("ID", 1, "NAME", "Test"));
        Map<String, Object> results = Map.of("PVIEW", expectedResults);
        
        when(jdbcTemplate.getDataSource()).thenReturn(dataSource);
        when(simpleJdbcCall.withSchemaName(anyString())).thenReturn(simpleJdbcCall);
        when(simpleJdbcCall.withCatalogName(anyString())).thenReturn(simpleJdbcCall);
        when(simpleJdbcCall.withProcedureName(anyString())).thenReturn(simpleJdbcCall);
        when(simpleJdbcCall.execute(any(MapSqlParameterSource.class))).thenReturn(results);
        
        // Create a static mock for SimpleJdbcCall constructor
        try (MockedStatic<SimpleJdbcCall> mockedStatic = Mockito.mockStatic(SimpleJdbcCall.class)) {
            // Configure the static mock
            mockedStatic.when(() -> new SimpleJdbcCall(jdbcTemplate)).thenReturn(simpleJdbcCall);
            
            // Act
            List<Map<String, Object>> result = repository.getProcResult(packageName, procedureName, params);
            
            // Assert
            assertEquals(expectedResults, result);
            verify(simpleJdbcCall).withSchemaName("TEST_SCHEMA");
            verify(simpleJdbcCall).withCatalogName(packageName);
            verify(simpleJdbcCall).withProcedureName(procedureName);
        }
    }

    @Test
    public void testQueryAsync() throws ExecutionException, InterruptedException {
        // Arrange
        String query = "SELECT * FROM TEST_TABLE WHERE ID = :id";
        Map<String, Object> params = Map.of("id", 1);
        List<Object> expectedResults = List.of(Map.of("ID", 1, "NAME", "Test"));
        
        when(rowMapperFactory.getRowMapper("testMapper")).thenReturn(rowMapper);
        when(namedParameterJdbcTemplate.query(eq(query), any(MapSqlParameterSource.class), eq(rowMapper))).thenReturn(expectedResults);

        // Act
        CompletableFuture<List<Object>> futureResults = repository.queryAsync(query, params, "testMapper");
        List<Object> results = futureResults.get();

        // Assert
        assertEquals(expectedResults, results);
        verify(namedParameterJdbcTemplate).query(eq(query), any(MapSqlParameterSource.class), eq(rowMapper));
    }

    @Test
    public void testHandleExceptionInQuery() {
        // Arrange
        String query = "SELECT * FROM TEST_TABLE WHERE ID = :id";
        Map<String, Object> params = Map.of("id", 1);
        
        when(rowMapperFactory.getRowMapper("testMapper")).thenReturn(rowMapper);
        when(namedParameterJdbcTemplate.query(eq(query), any(MapSqlParameterSource.class), eq(rowMapper)))
                .thenThrow(new DataAccessException("Test exception") {});

        // Act
        List<Object> results = repository.query(query, params, "testMapper");

        // Assert
        assertTrue(results.isEmpty());
        verify(namedParameterJdbcTemplate).query(eq(query), any(MapSqlParameterSource.class), eq(rowMapper));
    }
}