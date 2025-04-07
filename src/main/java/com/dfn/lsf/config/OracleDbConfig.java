package com.dfn.lsf.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.sql.DataSource;
import java.time.Duration;

/**
 * Oracle database configuration
 * Uses HikariCP for connection pooling with Java 21 optimizations
 */
@Configuration
public class OracleDbConfig {

    @Value("${spring.datasource.url}")
    private String jdbcUrl;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;

    @Value("${spring.datasource.driver-class-name:oracle.jdbc.OracleDriver}")
    private String driverClassName;

    @Value("${spring.datasource.hikari.maximum-pool-size:10}")
    private int maxPoolSize;

    @Value("${spring.datasource.hikari.minimum-idle:5}")
    private int minIdle;

    @Value("${spring.datasource.hikari.connection-timeout:30000}")
    private long connectionTimeout;

    @Value("${spring.datasource.hikari.idle-timeout:600000}")
    private long idleTimeout;

    /**
     * Creates the Oracle DataSource with HikariCP connection pooling
     * Optimized for Java 21 with appropriate settings
     *
     * @return DataSource instance
     */
    @Bean
    @Primary
    public DataSource dataSource() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(jdbcUrl);
        config.setUsername(username);
        config.setPassword(password);
        config.setDriverClassName(driverClassName);
        
        // Connection pool settings
        config.setMaximumPoolSize(maxPoolSize);
        config.setMinimumIdle(minIdle);
        config.setConnectionTimeout(connectionTimeout);
        config.setIdleTimeout(idleTimeout);
        
        // Java 21 optimizations
        config.setKeepaliveTime(Duration.ofMinutes(2).toMillis());
        
        // Oracle-specific settings
        config.addDataSourceProperty("oracle.jdbc.fanEnabled", "false");
        config.addDataSourceProperty("oracle.jdbc.implicitStatementCacheSize", "20");
        config.addDataSourceProperty("oracle.jdbc.maxCachedBufferSize", "100000");
        
        // Enable Oracle JDBC statement caching
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        
        return new HikariDataSource(config);
    }

    /**
     * Creates JdbcTemplate with configured DataSource
     *
     * @param dataSource The Oracle DataSource
     * @return JdbcTemplate instance
     */
    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        // Set query timeout to avoid hanging connections
        jdbcTemplate.setQueryTimeout(30);
        return jdbcTemplate;
    }

    /**
     * Creates NamedParameterJdbcTemplate with configured DataSource
     *
     * @param dataSource The Oracle DataSource
     * @return NamedParameterJdbcTemplate instance
     */
    @Bean
    public NamedParameterJdbcTemplate namedParameterJdbcTemplate(DataSource dataSource) {
        return new NamedParameterJdbcTemplate(dataSource);
    }
}