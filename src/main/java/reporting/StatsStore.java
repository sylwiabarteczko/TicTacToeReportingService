package reporting;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class StatsStore {

    private final StatsRepository repository;
    private final Set<String> finishedGameIds = ConcurrentHashMap.newKeySet();
    private final Set<String> createdGameIds = ConcurrentHashMap.newKeySet();
    private final ConcurrentHashMap<Integer, AtomicInteger> ageHistogram = new ConcurrentHashMap<>();

    public StatsStore(StatsRepository repository) {
        this.repository = repository;
        if (repository.count() == 0) {
            repository.save(new StatsEntity(0, 0, 0, 0));
        }
    }

    @Transactional
    public void handle(EventEnvelope event) {

        StatsEntity stats = repository.findById(1L)
                .orElseGet(() -> repository.save(new StatsEntity(0, 0, 0, 0)));

        switch (event.eventType()) {

            case USER_REGISTERED -> {
                stats.setRegisteredUsers(stats.getRegisteredUsers() + 1);
                Object ageObj = event.payload().get("age");
                if (ageObj instanceof Integer age) {
                    ageHistogram.computeIfAbsent(age, k -> new AtomicInteger(0)).incrementAndGet();
                }
            }
            case GAME_CREATED -> {
                stats.setGamesCreated(stats.getGamesCreated() + 1);
                String gameId = String.valueOf(event.payload().get("gameId"));
                createdGameIds.add(gameId);
                if (Boolean.TRUE.equals(event.payload().get("isAi"))) {
                    stats.setAiGames(stats.getAiGames() + 1);
                }
        }
            case GAME_FINISHED -> {
                stats.setGamesFinished(stats.getGamesFinished() + 1);
                String gameId = String.valueOf(event.payload().get("gameId"));
                finishedGameIds.add(gameId);
            }
            default -> {}
        }
        repository.save(stats);

        System.out.println("📊 STATS:");
        System.out.println("Users: " + stats.getRegisteredUsers());
        System.out.println("Games created: " + stats.getGamesCreated());
        System.out.println("Games finished: " + stats.getGamesFinished());
        System.out.println("Abandoned: " + getAbandonedGames());
        System.out.println("AI games: " + stats.getAiGames());
        System.out.println("------------");
    }

    public int getRegisteredUsers() {
        return repository.findById(1L).map(StatsEntity::getRegisteredUsers).orElse(0);
    }

    public int getGamesCreated() {
        return repository.findById(1L).map(StatsEntity::getGamesCreated).orElse(0);
    }

    public int getGamesFinished() {
        return repository.findById(1L).map(StatsEntity::getGamesFinished).orElse(0);
    }

    public int getAbandonedGames() {
        return (int) createdGameIds.stream()
                .filter(id -> !finishedGameIds.contains(id))
                .count();
    }

    public double getAiGamesPercentage() {
        int created = getGamesCreated();
        if (created == 0) return 0.0;
        return (double) repository.findById(1L).map(StatsEntity::getAiGames).orElse(0) / created * 100;
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

