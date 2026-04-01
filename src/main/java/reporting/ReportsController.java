package reporting;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;

@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174"})
@RestController
@RequestMapping("/api/reports")
public class ReportsController {

    private final StatsStore statsStore;

    public ReportsController(StatsStore statsStore) {
        this.statsStore = statsStore;
    }

    @GetMapping("/summary")
    public SummaryResponse summary() {
        return new SummaryResponse(
                statsStore.getRegisteredUsers(),
                statsStore.getGamesCreated(),
                statsStore.getGamesFinished(),
                statsStore.getAbandonedGames(),
                statsStore.getAiGamesPercentage()
        );
    }
    @GetMapping("/abandoned")
    public int abandoned() {
        return statsStore.getAbandonedGames();
    }

    @GetMapping("/ai-percentage")
    public double aiPercentage() {
        return statsStore.getAiGamesPercentage();
    }
    @GetMapping("/age-histogram")
    public Map<Integer, Integer> ageHistogram() {
        Map<Integer, Integer> result = new TreeMap<>();
        statsStore.getAgeHistogram().forEach((age, count) -> result.put(age, count.get()));
        return result;
    }
    @GetMapping("/registrations-by-day")
    public Map<String, Integer> registrationsByDay() {
        Map<String, Integer> result = new LinkedHashMap<>();
        List<String> order = List.of("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun");
        order.forEach(day -> {
            AtomicInteger count = statsStore.getRegistrationByDayOfWeek().get(day);
            result.put(day, count != null ? count.get() : 0);
        });
        return result;
    }
    @GetMapping("/average-moves")
    public double averageMoves() {
        return statsStore.getAverageMoves();
    }

}