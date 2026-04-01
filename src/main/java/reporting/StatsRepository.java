package reporting;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface StatsRepository extends JpaRepository<StatsEntity, Long> {

    @Modifying
    @Query("UPDATE StatsEntity s SET s.registeredUsers = s.registeredUsers + 1 WHERE s.id = 1")
    void incrementRegisteredUsers();

    @Modifying
    @Query("UPDATE StatsEntity s SET s.gamesCreated = s.gamesCreated + 1 WHERE s.id = 1")
    void incrementGamesCreated();

    @Modifying
    @Query("UPDATE StatsEntity s SET s.gamesFinished = s.gamesFinished + 1 WHERE s.id = 1")
    void incrementGamesFinished();

    @Modifying
    @Query("UPDATE StatsEntity s SET s.aiGames = s.aiGames + 1 WHERE s.id = 1")
    void incrementAiGames();

}
