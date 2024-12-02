package com.example.demo;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.oracle.OracleContainer;

public interface OracleContainerConfiguration {

  @Container
  OracleContainer oracleContainer =
      new OracleContainer("gvenzl/oracle-free:slim-faststart").withReuse(true);

  @DynamicPropertySource
  private static void configureProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.secondary.url", oracleContainer::getJdbcUrl);
    registry.add("spring.datasource.secondary.username", oracleContainer::getUsername);
    registry.add("spring.datasource.secondary.password", oracleContainer::getPassword);
  }
}
