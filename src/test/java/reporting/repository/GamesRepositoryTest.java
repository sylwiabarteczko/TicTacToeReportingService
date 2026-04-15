package reporting.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.junit.jupiter.Testcontainers;
import reporting.model.ReportingGame;
import reporting.model.ReportingUser;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
@Testcontainers
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(classes = reporting.ConsumerApplication.class)
public class GamesRepositoryTest {

    @Autowired
    GamesRepository gamesRepository;
    @Autowired
    private MovesRepository movesRepository;
    @Autowired
    private AiErrorsRepository aiErrorsRepository;
    @Autowired
    private UsersRepository usersRepository;

    @BeforeEach
    void setUp() {
        aiErrorsRepository.deleteAll();
        movesRepository.deleteAll();
        gamesRepository.deleteAll();
        usersRepository.deleteAll();
    }
    @Test
    void shouldCountFinishedAndAbandonedGames() {
        ReportingUser user = usersRepository.save(new ReportingUser(Instant.now()));

        gamesRepository.save(new ReportingGame(1L, user.getId(), Instant.now(), false));
        gamesRepository.save(new ReportingGame(2L, user.getId(), Instant.now(), false));
        gamesRepository.markAsFinished(1L);

        assertThat(gamesRepository.countByFinishedTrue()).isEqualTo(1);
        assertThat(gamesRepository.countByFinishedFalse()).isEqualTo(1);
    }
    @Test
    void shouldCountAiGames() {
        ReportingUser user = usersRepository.save(new ReportingUser(Instant.now()));

        gamesRepository.save(new ReportingGame(1L, user.getId(), Instant.now(), true));
        gamesRepository.save(new ReportingGame(2L, user.getId(), Instant.now(), false));

        assertThat(gamesRepository.countAiGames()).isEqualTo(1);
    }
    @Test
    void markAsFinished_shouldUpdateFlag() {
        ReportingUser user = usersRepository.save(new ReportingUser(Instant.now()));

        gamesRepository.save(new ReportingGame(5L, user.getId(), Instant.now(), false));
        gamesRepository.markAsFinished(5L);

        assertThat(gamesRepository.findById(5L))
                .isPresent()
                .get()
                .extracting(ReportingGame::isFinished)
                .isEqualTo(true);
    }
}