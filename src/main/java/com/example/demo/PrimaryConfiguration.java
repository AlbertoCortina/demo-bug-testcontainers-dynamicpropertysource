package com.example.demo;

import jakarta.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
    basePackages = "com.example.demo.primary",
    entityManagerFactoryRef = "primaryEntityManagerFactory",
    transactionManagerRef = "primaryTransactionManager")
public class PrimaryConfiguration {

  @Primary
  @Bean
  @ConfigurationProperties("spring.datasource.primary")
  public DataSourceProperties primaryDataSourceProperties() {
    return new DataSourceProperties();
  }

  @Primary
  @Bean
  public DataSource primaryDataSource() {
    return primaryDataSourceProperties().initializeDataSourceBuilder().build();
  }

  @Primary
  @Bean
  public LocalContainerEntityManagerFactoryBean primaryEntityManagerFactory(
      @Qualifier("primaryDataSource") DataSource dataSource, JpaProperties jpaProperties) {
    var em = new LocalContainerEntityManagerFactoryBean();
    em.setDataSource(dataSource);
    em.setPackagesToScan("com.example.demo.primary");

    var vendorAdapter = new HibernateJpaVendorAdapter();
    em.setJpaVendorAdapter(vendorAdapter);

    var props = jpaProperties.getProperties();
    props.put("hibernate.dialect", "org.hibernate.dialect.OracleDialect");

    em.setJpaPropertyMap(props);

    return em;
  }

  @Primary
  @Bean
  public PlatformTransactionManager primaryTransactionManager(
      @Qualifier("primaryEntityManagerFactory") EntityManagerFactory primaryEntityManagerFactory) {
    return new JpaTransactionManager(primaryEntityManagerFactory);
  }
}
