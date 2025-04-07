package com.dfn.lsf.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

/**
 * Loads and provides named SQL queries from properties files
 * Centralizes SQL query definitions for easier management and reuse
 */
@Component
public class QueryDefinitions {
    
    private static final Logger log = LoggerFactory.getLogger(QueryDefinitions.class);
    
    private final ResourceLoader resourceLoader;
    private final Map<String, String> queries = new HashMap<>();
    
    @Value("${lsf.queries.files:classpath:queries/lsf-queries.properties,classpath:queries/custom-queries.properties}")
    private String[] queryFiles;
    
    public QueryDefinitions(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }
    
    @PostConstruct
    public void init() {
        for (String file : queryFiles) {
            loadQueries(file);
        }
        log.info("Loaded {} SQL queries from configuration", queries.size());
    }
    
    /**
     * Load queries from a properties file
     * 
     * @param path Resource path
     */
    private void loadQueries(String path) {
        try {
            Resource resource = resourceLoader.getResource(path);
            
            if (!resource.exists()) {
                log.warn("Query file not found: {}", path);
                return;
            }
            
            Properties props = new Properties();
            try (InputStream is = resource.getInputStream()) {
                props.load(is);
            }
            
            for (String name : props.stringPropertyNames()) {
                String value = props.getProperty(name);
                if (value != null && !value.isBlank()) {
                    // Format SQL query - remove excess whitespace
                    value = value.replaceAll("\\s+", " ").trim();
                    queries.put(name, value);
                }
            }
            
            log.info("Loaded queries from file: {}", path);
        } catch (IOException e) {
            log.error("Error loading queries from: {}", path, e);
        }
    }
    
    /**
     * Get all registered queries
     * 
     * @return Unmodifiable map of query name to query text
     */
    public Map<String, String> getQueries() {
        return Collections.unmodifiableMap(queries);
    }
    
    /**
     * Get a specific query by name
     * 
     * @param name Query name
     * @return Query text or null if not found
     */
    public String getQuery(String name) {
        return queries.get(name);
    }
    
    /**
     * Register a new query at runtime
     * 
     * @param name Query name
     * @param query Query text
     */
    public void registerQuery(String name, String query) {
        if (query != null && !query.isBlank()) {
            // Format SQL query - remove excess whitespace
            query = query.replaceAll("\\s+", " ").trim();
            queries.put(name, query);
            log.info("Registered dynamic query: {}", name);
        }
    }
}