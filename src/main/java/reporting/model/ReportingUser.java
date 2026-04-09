package reporting.model;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "reporting_users")
public class ReportingUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "registered_at", nullable = false)
    private Instant registeredAt;
    public ReportingUser() {
    }
    public ReportingUser(Instant registeredAt) {
        this.registeredAt = registeredAt;
    }
    public Long getId() {
        return id;
    }
    public Instant getRegisteredAt() {
        return registeredAt;
    }
}
