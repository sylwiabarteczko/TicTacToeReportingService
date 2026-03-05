package reporting;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class EventsConsumer {

    private final ObjectMapper objectMapper;
    private final StatsStore statsStore;

    public EventsConsumer(ObjectMapper objectMapper, StatsStore statsStore) {
        this.objectMapper = objectMapper;
        this.statsStore = statsStore;
    }

    @KafkaListener(
            topics = "${tictactoe.kafka.topic:tictactoe.events}",
            groupId = "${tictactoe.kafka.groupId:tictactoe-reporting}"
    )
    public void onMessage(String message) {
        System.out.println("📩 Kafka message received: " + message);
        //tu ten komunikat powinien przyjsc, breakpoint i powinnam zobaczyc
        try {
            EventEnvelope event = objectMapper.readValue(message, EventEnvelope.class);

            statsStore.handle(event);

        } catch (Exception e) {
            System.err.println("Cannot parse Kafka message as EventEnvelope. Raw: " + message);
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
