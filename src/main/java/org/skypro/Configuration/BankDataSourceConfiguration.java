package org.skypro.Configuration;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Configuration
public class BankDataSourceConfiguration {

    @Bean(name = "BankDataSource")
    public DataSource bankDataSource(@Value("${application.bank-db.url}") String recommendationsUrl) {
        var dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(recommendationsUrl);
        dataSource.setDriverClassName("org.h2.Driver");
        dataSource.setReadOnly(true);
        return dataSource;
    }

    @Bean(name = "BankJdbcTemplate")
    public JdbcTemplate bankJdbcTemplate(@Qualifier("BankDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }
}
