package reporting;

import org.springframework.stereotype.Component;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class StatsStore {

    private final AtomicInteger registeredUsers = new AtomicInteger(0);
    private final AtomicInteger gamesCreated = new AtomicInteger(0);
    private final AtomicInteger gamesFinished = new AtomicInteger(0);

    public void handle(EventEnvelope event) {

        switch (event.eventType()) {

            case USER_REGISTERED -> registeredUsers.incrementAndGet();

            case GAME_CREATED -> gamesCreated.incrementAndGet();

            case GAME_FINISHED -> gamesFinished.incrementAndGet();

            default -> {

            }
        }

        System.out.println("📊 STATS:");
        System.out.println("Users: " + registeredUsers.get());
        System.out.println("Games created: " + gamesCreated.get());
        System.out.println("Games finished: " + gamesFinished.get());
        System.out.println("------------");
    }

    public int getRegisteredUsers() {
        return registeredUsers.get();
    }

    public int getGamesCreated() {
        return gamesCreated.get();
    }

    public int getGamesFinished() {
        return gamesFinished.get();
    }
}
