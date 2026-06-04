package com.jobtracker.api.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.net.URI;

@Configuration
@ConditionalOnProperty(name = "DATABASE_URL")
public class DataSourceConfig {

    @Value("${DATABASE_URL}")
    private String databaseUrl;

    @Bean
    @Primary
    public DataSource dataSource() throws Exception {
        URI uri = new URI(databaseUrl.replace("postgresql://", "http://")
                                     .replace("postgres://", "http://"));

        String host = uri.getHost();
        int port = uri.getPort() == -1 ? 5432 : uri.getPort();
        String path = uri.getPath().replaceFirst("^/", "");
        String[] userInfo = uri.getUserInfo().split(":");

        String jdbcUrl = String.format("jdbc:postgresql://%s:%d/%s", host, port, path);

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(jdbcUrl);
        config.setUsername(userInfo[0]);
        config.setPassword(userInfo[1]);
        config.setDriverClassName("org.postgresql.Driver");
        return new HikariDataSource(config);
    }
}
