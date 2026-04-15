package reporting;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reporting.model.ReportingAiError;
import reporting.model.ReportingGame;
import reporting.model.ReportingMove;
import reporting.model.ReportingUser;
import reporting.repository.AiErrorsRepository;
import reporting.repository.GamesRepository;
import reporting.repository.MovesRepository;
import reporting.repository.UsersRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class StatsStore {

    private static final Logger log = LoggerFactory.getLogger(StatsStore.class);

    private final UsersRepository usersRepository;
    private final GamesRepository gamesRepository;
    private final MovesRepository movesRepository;
    private final AiErrorsRepository aiErrorsRepository;

    public StatsStore(UsersRepository usersRepository,
                      GamesRepository gamesRepository,
                      MovesRepository movesRepository,
                      AiErrorsRepository aiErrorsRepository) {
        this.usersRepository = usersRepository;
        this.gamesRepository = gamesRepository;
        this.movesRepository = movesRepository;
        this.aiErrorsRepository = aiErrorsRepository;
    }

    @Transactional
    public void handle(EventEnvelope event) {

        switch (event.eventType()) {

            case USER_REGISTERED -> {
                usersRepository.save(new ReportingUser(event.occurredAt()));
            }
            case GAME_CREATED -> {
                Long gameId = toLong(event.payload().get("gameId"));
                Long userId = toLong(event.payload().get("userId"));
                boolean isAi = Boolean.TRUE.equals(event.payload().get("isAi"));
                gamesRepository.save(new ReportingGame(gameId, userId, event.occurredAt(), isAi));
            }
            case MOVE_MADE -> {
                Long gameId = toLong(event.payload().get("gameId"));
                movesRepository.save(new ReportingMove(gameId, event.occurredAt()));
            }
            case GAME_FINISHED -> {
                Long gameId = toLong(event.payload().get("gameId"));
                gamesRepository.markAsFinished(gameId);
            }
            case AI_ERROR -> {
                Long gameId = toLong(event.payload().get("gameId"));
                String message = String.valueOf(event.payload().get("message"));
                aiErrorsRepository.save(new ReportingAiError(gameId, event.occurredAt(), message));
            }
            default -> {
            }
        }

        log.info("📊 STATS: users={} games={} finished={} abandoned={}",
                getRegisteredUsers(), getGamesCreated(), getGamesFinished(), getAbandonedGames());
    }

    public int getRegisteredUsers() {
        return (int) usersRepository.count();
    }

    public int getGamesCreated() {
        return (int) gamesRepository.count();
    }

    public int getGamesFinished() {
        return (int) gamesRepository.countByFinishedTrue();
    }

    public int getAbandonedGames() {
        return (int) gamesRepository.countByFinishedFalse();
    }

    public int getAiGamesCount() {
        return (int) gamesRepository.countAiGames();
    }

    public double getAiGamesPercentage() {
        int created = getGamesCreated();
        if (created == 0) return 0.0;
        return (double) getAiGamesCount() / created * 100;
    }

    public double getAverageMoves() {
        Double avg = movesRepository.findAverageMoveCount();
        return avg != null ? avg : 0.0;
    }

    private Long toLong(Object value) {
        if (value instanceof Number n) return n.longValue();
        if (value instanceof String s) return Long.parseLong(s);
        return null;
    }
}

