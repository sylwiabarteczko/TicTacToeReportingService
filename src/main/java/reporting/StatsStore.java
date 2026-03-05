package reporting;

import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class StatsStore {

    private final AtomicInteger registeredUsers = new AtomicInteger(0);
    private final AtomicInteger gamesCreated = new AtomicInteger(0);
    private final AtomicInteger gamesFinished = new AtomicInteger(0);
    private final AtomicInteger aiGames = new AtomicInteger(0);
    private final Set<String> finishedGameIds = ConcurrentHashMap.newKeySet();
    private final Set<String> createdGameIds = ConcurrentHashMap.newKeySet();
    private final ConcurrentHashMap<Integer, AtomicInteger> ageHistogram = new ConcurrentHashMap<>();

    public void handle(EventEnvelope event) {

        switch (event.eventType()) {

            case USER_REGISTERED -> {
                registeredUsers.incrementAndGet();
                Object ageObj = event.payload().get("age");
                if (ageObj instanceof Integer age) {
                    ageHistogram.computeIfAbsent(age, k -> new AtomicInteger(0)).incrementAndGet();
                }
            }
            case GAME_CREATED -> {
                gamesCreated.incrementAndGet();

            String gameId = String.valueOf(event.payload().get("gameId"));
            createdGameIds.add(gameId);
            Object isAi = event.payload().get("isAi");
            if (Boolean.TRUE.equals(isAi)) {
                aiGames.incrementAndGet();
            }
        }
            case GAME_FINISHED -> {
                gamesFinished.incrementAndGet();
                String gameId = String.valueOf(event.payload().get("gameId"));
                finishedGameIds.add(gameId);
            }
            default -> {}
        }

        System.out.println("📊 STATS:");
        System.out.println("Users: " + registeredUsers.get());
        System.out.println("Games created: " + gamesCreated.get());
        System.out.println("Games finished: " + gamesFinished.get());
        System.out.println("Abandoned: " + getAbandonedGames());
        System.out.println("AI games: " + aiGames.get());
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
    public int getAbandonedGames() {
        return (int) createdGameIds.stream()
                .filter(id -> !finishedGameIds.contains(id))
                .count();
    }
    public double getAiGamesPercentage() {
        if (gamesCreated.get() == 0) return 0.0;
        return (double) aiGames.get() / gamesCreated.get() * 100;
    }
    public ConcurrentHashMap<Integer, AtomicInteger> getAgeHistogram() {
        return ageHistogram;
    }
}
// wiek przy rejestracji -histogram/rozklad wieku
// % gier rozegranych z AI,
//ile gier sie zakonczylo, a ile zostalo porzuconych
// normalny gracz nie ma dostepu, to tylko dla admin
// najpierw wrzucanie i odbieranie z kafki

