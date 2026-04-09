package reporting.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "reporting_moves")
public class ReportingMove {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "game_id", nullable = false)
    private Long gameId;
    @Column(name = "moved_at", nullable = false)
    private Instant movedAt;
    public ReportingMove() {
    }
    public ReportingMove(Long gameId, Instant movedAt) {
        this.gameId = gameId;
        this.movedAt = movedAt;
    }
    public Long getId() {
        return id;
    }
    public Long getGameId() {
        return gameId;
    }
    public Instant getMovedAt() {
        return movedAt;
    }
}
