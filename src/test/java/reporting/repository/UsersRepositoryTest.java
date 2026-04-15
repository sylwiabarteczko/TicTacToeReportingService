package reporting.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import reporting.model.ReportingUser;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(classes = reporting.ConsumerApplication.class)
class UsersRepositoryTest {

    @Container
    static PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:latest")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
    }

    @Autowired
    private UsersRepository usersRepository;

    @BeforeEach
    void setUp() {
        usersRepository.deleteAll();
    }

    @Test
    void shouldSaveAndCountUsers() {
        usersRepository.save(new ReportingUser(Instant.now()));
        usersRepository.save(new ReportingUser(Instant.now()));

        assertThat(usersRepository.count()).isEqualTo(2);
    }

    @Test
    void countByMonth_shouldGroupByMonth() {
        usersRepository.save(new ReportingUser(Instant.parse("2024-01-15T10:00:00Z")));
        usersRepository.save(new ReportingUser(Instant.parse("2024-01-20T10:00:00Z")));
        usersRepository.save(new ReportingUser(Instant.parse("2024-03-10T10:00:00Z")));

        List<Object[]> result = usersRepository.countByMonth();

        assertThat(result).hasSize(2);
    }
}