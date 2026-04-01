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
    private final ConcurrentHashMap<String, AtomicInteger> registrationByDayOfWeek = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, AtomicInteger> moveCountPerGame = new ConcurrentHashMap<>();
    public StatsStore(StatsRepository repository) {
        this.repository = repository;
        if (repository.count() == 0) {
            repository.save(new StatsEntity(0, 0, 0, 0));
        }
    }

    // flyway, chce miec tabele z grami
    // potrzebujemy tabele, 1 tabela z uzytkownikami, identyfikator uzytkownika, ilu sie rejestruje,
    // ile srednio gier tworzy uzytkownik, data kiedy uzytkownik dolaczyl,
    // 2 w momencie stworzenia gry, id, czy sie zakonczyla, czas utworzenia gry
    // 3. ile ruchow, identyfikator ruchu, czas wykonania ruchu
    // 4. ai error, id , czas bledu, komunikat bledu, moze tekstowy komunikat
    // czy umiejetnosci gracza rosna wraz z okresem uzytkowania
    //histogram wyznaczony z 1 tabeli, policzyc w jakim miesiacu uzytkownicy sie najczesciej rejestruja

    @Transactional
    public void handle(EventEnvelope event) {

        switch (event.eventType()) {

            case USER_REGISTERED -> {
                repository.incrementRegisteredUsers();
                Object ageObj = event.payload().get("age");
                if (ageObj instanceof Integer age) {
                    ageHistogram.computeIfAbsent(age, k -> new AtomicInteger(0)).incrementAndGet();
                }
                String day = java.time.LocalDate.now()
                        .getDayOfWeek()
                        .getDisplayName(java.time.format.TextStyle.SHORT, java.util.Locale.ENGLISH);
                registrationByDayOfWeek.computeIfAbsent(day, k -> new AtomicInteger(0)).incrementAndGet();
            }
            case GAME_CREATED -> {
                repository.incrementGamesCreated();
                String gameId = String.valueOf(event.payload().get("gameId"));
                createdGameIds.add(gameId);
                if (Boolean.TRUE.equals(event.payload().get("isAi"))) {
                    repository.incrementAiGames();
                }
            }
            case MOVE_MADE -> {
                String gameId = String.valueOf(event.payload().get("gameId"));
                moveCountPerGame.computeIfAbsent(gameId, k -> new AtomicInteger(0)).incrementAndGet();
            }
            case GAME_FINISHED -> {
                repository.incrementGamesFinished();
                String gameId = String.valueOf(event.payload().get("gameId"));
                finishedGameIds.add(gameId);
            }
            case AI_ERROR -> {
                System.out.println("⚠️ AI_ERROR for game: " + event.payload().get("gameId"));
            }
            default -> {}
        }

        System.out.println("📊 STATS:");
        System.out.println("Users: " + getRegisteredUsers());
        System.out.println("Games created: " + getGamesCreated());
        System.out.println("Games finished: " + getGamesFinished());
        System.out.println("Abandoned: " + getAbandonedGames());
        System.out.println("AI games: " + getAiGamesCount());
        System.out.println("------------");
    }

    public int getRegisteredUsers() {
        return repository.findAll().stream().findFirst()
                .map(StatsEntity::getRegisteredUsers).orElse(0);
    }

    public int getGamesCreated() {
        return repository.findAll().stream().findFirst()
                .map(StatsEntity::getGamesCreated).orElse(0);
    }

    public int getGamesFinished() {
        return repository.findAll().stream().findFirst()
                .map(StatsEntity::getGamesFinished).orElse(0);
    }

    public int getAbandonedGames() {
        return (int) createdGameIds.stream()
                .filter(id -> !finishedGameIds.contains(id))
                .count();
    }

    public double getAiGamesPercentage() {
        int created = getGamesCreated();
        if (created == 0) return 0.0;
        return (double) getAiGamesCount() / created * 100;
    }
    public int getAiGamesCount() {
        return repository.findAll().stream().findFirst()
                .map(StatsEntity::getAiGames).orElse(0);
    }
    public double getAverageMoves() {
        if (moveCountPerGame.isEmpty()) return 0.0;
        return moveCountPerGame.values().stream()
                .mapToInt(AtomicInteger::get)
                .average()
                .orElse(0.0);
    }

    public ConcurrentHashMap<Integer, AtomicInteger> getAgeHistogram() {
        return ageHistogram;
    }
    public ConcurrentHashMap<String, AtomicInteger> getRegistrationByDayOfWeek() {
        return registrationByDayOfWeek;
    }
}
// wiek przy rejestracji -histogram/rozklad wieku
// % gier rozegranych z AI,
//ile gier sie zakonczylo, a ile zostalo porzuconych
// normalny gracz nie ma dostepu, to tylko dla admin
// najpierw wrzucanie i odbieranie z kafki

