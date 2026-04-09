package reporting.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reporting.StatsStore;
import reporting.SummaryResponse;

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

    @GetMapping("/average-moves")
    public double averageMoves() {
        return statsStore.getAverageMoves();
    }

}