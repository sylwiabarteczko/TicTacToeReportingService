package reporting.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import reporting.model.ReportingGame;

public interface GamesRepository extends JpaRepository<ReportingGame, Long> {

    long countByFinishedFalse();
    long countByFinishedTrue();

    @Query("SELECT COUNT(g) FROM ReportingGame g WHERE g.isAi = true")
    long countAiGames();

    @Modifying(clearAutomatically = true)
    @Query("UPDATE ReportingGame g SET g.finished = true WHERE g.id = :gameId")
    void markAsFinished(Long gameId);
}
