package org.skypro.Configuration;

import liquibase.integration.spring.SpringLiquibase;
import org.skypro.Repository.RepositoryForMyBase.MyDataBaseRepository;
import org.springframework.context.annotation.*;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
@EnableJpaRepositories(basePackages = "org.skypro.Repository",
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
                classes = MyDataBaseRepository.class )
        , entityManagerFactoryRef = "entityManagerFactory",
        transactionManagerRef = "transactionManager")
public class BankConfiguration {

    @Primary
    @Bean(name = "defaultDataSource")
    public DataSource defaultDataSource() {
        return DataSourceBuilder.create()
                .url("jdbc:h2:file:./transaction/transaction")
                .username("")
                .password("")
                .build();
    }

    @Primary
    @Bean(name = "entityManagerFactory")
    public LocalContainerEntityManagerFactoryBean primaryEntityManager(
            @Qualifier("defaultDataSource") DataSource dataSource) {

        LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
        factory.setDataSource(dataSource);
        factory.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        factory.setPackagesToScan("org.skypro.Entity"); // <-- Пакет с сущностями ДЛЯ ОСНОВНОЙ базы
        factory.setPersistenceUnitName("primary");
        return factory;
    }

    @Primary
    @Bean(name = "transactionManager")
    public PlatformTransactionManager primaryTransactionManager(
            @Qualifier("entityManagerFactory") EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }

    @Bean(name = "BankJdbcTemplate")
    public JdbcTemplate bankJdbcTemplate(@Qualifier("defaultDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }


}
