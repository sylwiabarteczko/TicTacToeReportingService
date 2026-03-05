package reporting;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.TreeMap;

@CrossOrigin(origins = "http://localhost:5173")
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
// endpoint do wystawiania tej konkretnej statystyki
}