package de.hennywilly.springhibernatemysql;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import javax.sql.DataSource;

import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@ContextConfiguration
@Testcontainers
class MainTests {

    private static final String DB = "testDB";
    private static final String USER = "testUser";
    private static final String PW = "testPW";

    private static final String MYSQL_IMAGE_VERSION = "8.2.0-oracle";
    private static final DockerImageName MYSQL_DOCKER_IMAGE_NAME = DockerImageName
            .parse(MySQLContainer.NAME)
            .withTag(MYSQL_IMAGE_VERSION);
    private static final MySQLContainer<?> MY_SQL_CONTAINER = new MySQLContainer<>(MYSQL_DOCKER_IMAGE_NAME)
            .withDatabaseName(DB)
            .withUsername(USER)
            .withPassword(PW);

    @BeforeAll
    static void beforeAll() {
        MY_SQL_CONTAINER.start();
    }

    @AfterAll
    static void afterAll() {
        MY_SQL_CONTAINER.stop();
    }

    @Test
    void contextLoads() {
        // Should explode before
        assertTrue(true);
    }

    @TestConfiguration
    public static class MainTestsConfig {

        @Bean
        public DataSource dataSource() {
            return DataSourceBuilder.create()
                    .driverClassName(com.mysql.cj.jdbc.Driver.class.getName())
                    .url(MY_SQL_CONTAINER.getJdbcUrl())
                    .username(USER)
                    .password(PW)
                    .build();
        }

        @Bean
        public JpaVendorAdapter jpaVendorAdapter() {
            HibernateJpaVendorAdapter hibernateJpaVendorAdapter = new HibernateJpaVendorAdapter();
            hibernateJpaVendorAdapter.setDatabase(Database.MYSQL);
            return hibernateJpaVendorAdapter;
        }

        @Bean
        public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource,
                                                                           JpaVendorAdapter jpaVendorAdapter) {
            LocalContainerEntityManagerFactoryBean lef = new LocalContainerEntityManagerFactoryBean();
            lef.setDataSource(dataSource);
            lef.setJpaVendorAdapter(jpaVendorAdapter);
            lef.setPackagesToScan(Main.class.getPackage().getName());
            return lef;
        }
    }
}
