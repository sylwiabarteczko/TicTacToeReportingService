package reporting.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "reporting_games")
public class ReportingGame {

    @Id
    private Long id;
    @Column(name = "user_id")
    private Long userId;
    @Column(name ="created_at", nullable = false)
    private Instant createdAt;
    @Column(name = "finished", nullable = false)
    private boolean finished = false;
    @Column(name = "is_ai", nullable = false)
    private boolean isAi = false;
    public ReportingGame() {
    }
    public ReportingGame(Long id, Long userId, Instant createdAt, boolean isAi) {
        this.id = id;
        this.userId = userId;
        this.createdAt = createdAt;
        this.isAi = isAi;
    }
    public Long getId() {
        return id;
    }
    public Long getUserId() {
        return userId;
    }
    public Instant getCreatedAt() {
        return createdAt;
    }
    public boolean isFinished() {
        return finished;
    }
    public void setFinished(boolean finished) {
        this.finished = finished;
    }
    public boolean isAi() {
        return isAi;
    }


}
