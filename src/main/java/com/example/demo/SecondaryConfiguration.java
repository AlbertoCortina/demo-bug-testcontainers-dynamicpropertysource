package com.example.demo;

import jakarta.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
    basePackages = "com.example.demo.secondary",
    entityManagerFactoryRef = "secondaryEntityManagerFactory",
    transactionManagerRef = "secondaryTransactionManager")
public class SecondaryConfiguration {

  @Bean
  @ConfigurationProperties("spring.datasource.secondary")
  public DataSourceProperties secondaryDataSourceProperties() {
    return new DataSourceProperties();
  }

  @Bean
  public DataSource secondaryDataSource() {
    return secondaryDataSourceProperties().initializeDataSourceBuilder().build();
  }

  @Bean
  public LocalContainerEntityManagerFactoryBean secondaryEntityManagerFactory(
      @Qualifier("secondaryDataSource") DataSource dataSource, JpaProperties jpaProperties) {
    var em = new LocalContainerEntityManagerFactoryBean();
    em.setDataSource(dataSource);
    em.setPackagesToScan("com.example.demo.secondary");

    var vendorAdapter = new HibernateJpaVendorAdapter();
    em.setJpaVendorAdapter(vendorAdapter);

    var props = jpaProperties.getProperties();
    props.put("hibernate.dialect", "org.hibernate.dialect.OracleDialect");

    em.setJpaPropertyMap(props);

    return em;
  }

  @Bean
  public PlatformTransactionManager secondaryTransactionManager(
      @Qualifier("secondaryEntityManagerFactory")
          EntityManagerFactory secondaryEntityManagerFactory) {
    return new JpaTransactionManager(secondaryEntityManagerFactory);
  }
}
