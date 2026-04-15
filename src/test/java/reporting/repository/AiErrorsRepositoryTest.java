package reporting.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.junit.jupiter.Testcontainers;

import reporting.model.ReportingAiError;
import reporting.model.ReportingGame;
import reporting.model.ReportingUser;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
@Testcontainers
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(classes = reporting.ConsumerApplication.class)
class AiErrorsRepositoryTest {

    @Autowired
    AiErrorsRepository aiErrorsRepository;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private GamesRepository gamesRepository;

    @BeforeEach
    void setUp() {
        aiErrorsRepository.deleteAll();
        gamesRepository.deleteAll();
        usersRepository.deleteAll();
    }
    @Test
    void shouldSaveAndRetrieveAiError() {
        ReportingUser user = usersRepository.save(new ReportingUser(Instant.now()));
        gamesRepository.save(new ReportingGame(42L, user.getId(), Instant.now(), true));

        aiErrorsRepository.save(new ReportingAiError(42L, Instant.now(), "OpenRouter timeout"));

        assertThat(aiErrorsRepository.count()).isEqualTo(1);
        assertThat(aiErrorsRepository.findAll().get(0).getMessage())
                .isEqualTo("OpenRouter timeout");
    }

    @Test
    void shouldSaveMultipleErrorsForSameGame() {
        ReportingUser user = usersRepository.save(new ReportingUser(Instant.now()));
        gamesRepository.save(new ReportingGame(7L, user.getId(), Instant.now(), true));

        aiErrorsRepository.save(new ReportingAiError(7L, Instant.now(), "error 1"));
        aiErrorsRepository.save(new ReportingAiError(7L, Instant.now(), "error 2"));

        assertThat(aiErrorsRepository.count()).isEqualTo(2);
    }


}
