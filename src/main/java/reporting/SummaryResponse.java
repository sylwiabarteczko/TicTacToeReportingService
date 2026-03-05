package reporting;

public record SummaryResponse(
        int users,
        int gamesCreated,
        int gamesFinished,
        int abandoned,
        double aiPercentage) {
}
