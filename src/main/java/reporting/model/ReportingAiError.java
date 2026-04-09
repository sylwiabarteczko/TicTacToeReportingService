package reporting.model;

import jakarta.persistence.*;
import org.apache.kafka.common.protocol.types.Field;

import java.time.Instant;

@Entity
@Table(name = "reporting_ai_errors")
public class ReportingAiError {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "game_id")
    private Long gameId;
    @Column(name = "error_at", nullable = false)
    private Instant errorAt;
    @Column(name = "message")
    private String message;
    public ReportingAiError() {
    }
    public ReportingAiError(Long gameId, Instant errorAt, String message) {
        this.gameId = gameId;
        this.errorAt = errorAt;
        this.message = message;
    }
    public Long getId() {
        return id;
    }
    public Long getGameId() {
        return gameId;
    }
    public Instant getErrorAt() {
        return errorAt;
    }
    public String getMessage() {
        return message;
    }
}