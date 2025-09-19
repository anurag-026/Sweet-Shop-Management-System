package com.anurag.SweetShopBackend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

@Configuration
public class DataSourceConfig {

    @Value("${spring.datasource.url}")
    private String databaseUrl;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;

    @Bean
    @Primary
    public DataSource dataSource() {
        // Create database if it doesn't exist before creating DataSource
        createDatabaseIfNotExists();
        
        return DataSourceBuilder.create()
                .url(databaseUrl)
                .username(username)
                .password(password)
                .driverClassName("org.postgresql.Driver")
                .build();
    }

    private void createDatabaseIfNotExists() {
        try {
            // Extract database name from URL
            String dbName = extractDatabaseName(databaseUrl);
            String baseUrl = databaseUrl.substring(0, databaseUrl.lastIndexOf("/"));
            
            // Connect to postgres database to create our database
            String postgresUrl = baseUrl + "/postgres";
            
            try (Connection connection = DriverManager.getConnection(postgresUrl, username, password);
                 Statement statement = connection.createStatement()) {
                
                // Check if database exists
                String checkDbQuery = "SELECT 1 FROM pg_database WHERE datname = '" + dbName + "'";
                boolean dbExists = statement.executeQuery(checkDbQuery).next();
                
                if (!dbExists) {
                    // Create database
                    String createDbQuery = "CREATE DATABASE " + dbName;
                    statement.executeUpdate(createDbQuery);
                    System.out.println("✅ Database '" + dbName + "' created successfully!");
                } else {
                    System.out.println("ℹ️  Database '" + dbName + "' already exists.");
                }
            }
        } catch (Exception e) {
            System.err.println("❌ Error creating database: " + e.getMessage());
            throw new RuntimeException("Failed to create database", e);
        }
    }

    private String extractDatabaseName(String url) {
        // Extract database name from jdbc:postgresql://localhost:5432/sweetshop
        int lastSlash = url.lastIndexOf("/");
        int questionMark = url.indexOf("?");
        
        if (questionMark == -1) {
            return url.substring(lastSlash + 1);
        } else {
            return url.substring(lastSlash + 1, questionMark);
        }
    }
}
