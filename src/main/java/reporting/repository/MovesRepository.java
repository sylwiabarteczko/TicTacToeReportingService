package reporting.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import reporting.model.ReportingMove;

public interface MovesRepository extends JpaRepository<ReportingMove, Long> {

    long countByGameId(Long gameId);

    @Query("SELECT AVG(sub.cnt) FROM (SELECT COUNT(m) AS cnt FROM ReportingMove m GROUP BY m.gameId) sub")
    Double findAverageMoveCount();
}
