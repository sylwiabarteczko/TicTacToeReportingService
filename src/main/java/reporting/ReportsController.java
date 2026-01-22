package reporting;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
                statsStore.getGamesFinished()
        );
    }

}