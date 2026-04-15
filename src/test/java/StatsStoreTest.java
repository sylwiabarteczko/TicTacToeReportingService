import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reporting.EventEnvelope;
import reporting.EventType;
import reporting.StatsStore;

import java.util.Map;
import java.util.UUID;
import java.time.Instant;

import reporting.repository.AiErrorsRepository;
import reporting.repository.GamesRepository;
import reporting.repository.MovesRepository;
import reporting.repository.UsersRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class StatsStoreTest {

    @Mock
    UsersRepository usersRepository;
    @Mock
    GamesRepository gamesRepository;
    @Mock
    MovesRepository movesRepository;
    @Mock
    AiErrorsRepository aiErrorsRepository;
    @InjectMocks
    StatsStore statsStore;

    private EventEnvelope event(EventType type, Map<String, Object> payload) {
        return new EventEnvelope(UUID.randomUUID(), type, Instant.now(), 1, payload);
    }

    @Test
    void handle_userRegistered_shouldSaveUser() {
        statsStore.handle(event(EventType.USER_REGISTERED, Map.of()));

        verify(usersRepository).save(any());
    }

    @Test
    void handle_gameCreated_shouldSaveGame() {
        statsStore.handle(event(EventType.GAME_CREATED,
                Map.of("gameId", 1L, "userId", 10L, "isAi", false)));

        verify(gamesRepository).save(any());
    }
    @Test
    void handle_moveMade_shouldSaveMove() {
        statsStore.handle(event(EventType.MOVE_MADE, Map.of("gameId", 1L)));

        verify(movesRepository).save(any());
    }

    @Test
    void handle_gameFinished_shouldCallMarkAsFinished() {
        statsStore.handle(event(EventType.GAME_FINISHED, Map.of("gameId", 5L)));

        verify(gamesRepository).markAsFinished(5L);
        verify(gamesRepository, never()).save(any());
    }
    @Test
    void handle_aiError_shouldSaveError() {
        statsStore.handle(event(EventType.AI_ERROR,
                Map.of("gameId", 3L, "message", "timeout")));

        verify(aiErrorsRepository).save(any());
    }

    @Test
    void getAiGamesPercentage_shouldReturnZeroWhenNoGames() {
        when(gamesRepository.count()).thenReturn(0L);

        assertThat(statsStore.getAiGamesPercentage()).isEqualTo(0.0);
        verify(gamesRepository, never()).countAiGames();
    }
    @Test
    void getAiGamesPercentage_shouldCalculateCorrectly() {
        when(gamesRepository.count()).thenReturn(4L);
        when(gamesRepository.countAiGames()).thenReturn(1L);

        assertThat(statsStore.getAiGamesPercentage()).isEqualTo(25.0);
    }

    @Test
    void getAiGamesPercentage_shouldReturn100WhenAllAi() {
        when(gamesRepository.count()).thenReturn(3L);
        when(gamesRepository.countAiGames()).thenReturn(3L);

        assertThat(statsStore.getAiGamesPercentage()).isEqualTo(100.0);
    }

    @Test
    void getAverageMoves_shouldReturnValueFromRepository() {
        when(movesRepository.findAverageMoveCount()).thenReturn(7.5);

        assertThat(statsStore.getAverageMoves()).isEqualTo(7.5);
    }
    @Test
    void getAverageMoves_shouldReturnZeroWhenRepositoryReturnsNull() {
        when(movesRepository.findAverageMoveCount()).thenReturn(null);

        assertThat(statsStore.getAverageMoves()).isEqualTo(0.0);
    }

    @Test
    void getRegisteredUsers_shouldDelegateToRepository() {
        when(usersRepository.count()).thenReturn(7L);

        assertThat(statsStore.getRegisteredUsers()).isEqualTo(7);
    }

    @Test
    void getGamesFinished_shouldDelegateToRepository() {
        when(gamesRepository.countByFinishedTrue()).thenReturn(3L);

        assertThat(statsStore.getGamesFinished()).isEqualTo(3);
    }
    @Test
    void getAbandonedGames_shouldDelegateToRepository() {
        when(gamesRepository.countByFinishedFalse()).thenReturn(2L);

        assertThat(statsStore.getAbandonedGames()).isEqualTo(2);
    }
}

