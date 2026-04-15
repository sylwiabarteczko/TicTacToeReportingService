package reporting.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.junit.jupiter.Testcontainers;
import reporting.model.ReportingGame;
import reporting.model.ReportingMove;
import reporting.model.ReportingUser;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

@Testcontainers
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(classes = reporting.ConsumerApplication.class)
public class MovesRepositoryTest {

    @Autowired
    MovesRepository movesRepository;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private GamesRepository gamesRepository;

    @BeforeEach
    void setUp() {
        movesRepository.deleteAll();
        gamesRepository.deleteAll();
        usersRepository.deleteAll();
    }
    @Test
    void shouldCountMovesByGameId() {
        ReportingUser user = usersRepository.save(new ReportingUser(Instant.now()));
        gamesRepository.save(new ReportingGame(1L, user.getId(), Instant.now(), false));
        gamesRepository.save(new ReportingGame(2L, user.getId(), Instant.now(), false));

        movesRepository.save(new ReportingMove(1L, Instant.now()));
        movesRepository.save(new ReportingMove(1L, Instant.now()));
        movesRepository.save(new ReportingMove(2L, Instant.now()));

        assertThat(movesRepository.countByGameId(1L)).isEqualTo(2);
        assertThat(movesRepository.countByGameId(2L)).isEqualTo(1);
    }
    @Test
    void findAverageMoveCount_shouldReturnAveragePerGame() {
        ReportingUser user = usersRepository.save(new ReportingUser(Instant.now()));
        gamesRepository.save(new ReportingGame(1L, user.getId(), Instant.now(), false));
        gamesRepository.save(new ReportingGame(2L, user.getId(), Instant.now(), false));

        movesRepository.save(new ReportingMove(1L, Instant.now()));
        movesRepository.save(new ReportingMove(1L, Instant.now()));
        movesRepository.save(new ReportingMove(1L, Instant.now()));
        movesRepository.save(new ReportingMove(1L, Instant.now()));
        movesRepository.save(new ReportingMove(2L, Instant.now()));
        movesRepository.save(new ReportingMove(2L, Instant.now()));

        assertThat(movesRepository.findAverageMoveCount()).isCloseTo(3.0, within(0.01));
    }
    @Test
    void findAverageMoveCount_shouldReturnNullWhenNoMoves() {
        assertThat(movesRepository.findAverageMoveCount()).isNull();
    }

}
