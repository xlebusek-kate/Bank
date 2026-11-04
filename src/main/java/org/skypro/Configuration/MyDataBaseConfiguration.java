package org.skypro.Configuration;

import jakarta.persistence.EntityManagerFactory;
import liquibase.integration.spring.SpringLiquibase;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableJpaRepositories(basePackages = "org.skypro.Repository.RepositoryForMyBase",
             entityManagerFactoryRef = "myEM",
             transactionManagerRef = "myTM")
public class MyDataBaseConfiguration {

    @Bean("dataBase")
    public DataSource myDataSource() {
        return DataSourceBuilder
                .create()
                .url("jdbc:postgresql://localhost:5432/bank_database")
                .username("denisForBank")
                .password("denis123456789")
                .build();
    }

    @Bean("myEM")
    public LocalContainerEntityManagerFactoryBean myEntityManager(@Qualifier("dataBase") DataSource dataSource) {

        Map<String, Object> props = new HashMap<>();
        props.put("hibernate.physical_naming_strategy",
                "org.hibernate.boot.model.naming.CamelCaseToUnderscoresNamingStrategy");

        LocalContainerEntityManagerFactoryBean factoryBean = new LocalContainerEntityManagerFactoryBean();
    factoryBean.setDataSource(dataSource);
    factoryBean.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
    factoryBean.setPackagesToScan("org.skypro.Entity.EntityForMyBase");
    factoryBean.setJpaPropertyMap(props);
    return factoryBean;
    }

    @Bean("myTM")
    public PlatformTransactionManager myDataBaseTransactionManager(@Qualifier("myEM") EntityManagerFactory managerFactory) {
        return new JpaTransactionManager(managerFactory);
    }

    @Bean
    public SpringLiquibase liquibase (@Qualifier("dataBase") DataSource dataSource){
        SpringLiquibase springLiquibase = new SpringLiquibase();
        springLiquibase.setDataSource(dataSource);
        springLiquibase.setChangeLog("classpath:Liquibase/changelog-master.yml");
        return springLiquibase;
    }
}
