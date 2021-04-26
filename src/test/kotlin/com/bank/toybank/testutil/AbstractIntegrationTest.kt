package com.bank.toybank.testutil

import com.bank.toybank.ToyBankApplication
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.context.jdbc.Sql
import org.testcontainers.containers.PostgreSQLContainer

@ActiveProfiles("test")
@SpringBootTest(classes = [ToyBankApplication::class], webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = ["/sql-scripts/clear_data.sql"], executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
abstract class AbstractIntegrationTest {
    companion object {

        private val postgresqlContainer = KPostgreSQLContainer("postgres:12.3-alpine")
            .withDatabaseName("bank-db")
            .withUsername("user")
            .withPassword("password")
            .apply { start() }

        @JvmStatic
        @DynamicPropertySource
        fun setProperties(registry: DynamicPropertyRegistry) {
            mapOf(
                "spring.datasource.url" to postgresqlContainer.jdbcUrl,
                "spring.datasource.username" to postgresqlContainer.username,
                "spring.datasource.password" to postgresqlContainer.password,
                "spring.datasource.driver-class-name" to postgresqlContainer.driverClassName
            ).forEach { (k, v) -> registry.add(k) { v } }
        }
    }
}

private class KPostgreSQLContainer(imageName: String) : PostgreSQLContainer<KPostgreSQLContainer>(imageName)
