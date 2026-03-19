package reporting;

import jakarta.persistence.*;

@Entity
@Table(name = "stats_entity")
public class StatsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long id;
    private int registeredUsers;
    private int gamesCreated;
    private int gamesFinished;
    private int aiGames;

    public StatsEntity() {}

    public StatsEntity(int registeredUsers, int gamesCreated, int gamesFinished, int aiGames) {
        this.registeredUsers = registeredUsers;
        this.gamesCreated = gamesCreated;
        this.gamesFinished = gamesFinished;
        this.aiGames = aiGames;
    }
    public Long getId() { return id; }
    public int getRegisteredUsers() { return registeredUsers; }
    public void setRegisteredUsers(int v) { this.registeredUsers = v; }
    public int getGamesCreated() { return gamesCreated; }
    public void setGamesCreated(int v) { this.gamesCreated = v; }
    public int getGamesFinished() { return gamesFinished; }
    public void setGamesFinished(int v) { this.gamesFinished = v; }
    public int getAiGames() { return aiGames; }
    public void setAiGames(int v) { this.aiGames = v; }

}
