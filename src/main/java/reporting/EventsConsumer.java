package reporting;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class EventsConsumer {

    private final ObjectMapper objectMapper;

    public EventsConsumer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @KafkaListener(
            topics = "${tictactoe.kafka.topic:tictactoe.events}",
            groupId = "${tictactoe.kafka.groupId:tictactoe-reporting}"
    )
    public void onMessage(String message) {
        try {
            EventEnvelope env = objectMapper.readValue(message, EventEnvelope.class);

            System.out.println("EVENT TYPE: " + env.eventType());
            System.out.println("EVENT ID: " + env.eventId());
            System.out.println("AT: " + env.occurredAt());
            System.out.println("PAYLOAD: " + env.payload());

        } catch (Exception e) {
            System.err.println("Cannot parse Kafka message as EventEnvelope. Raw: " + message);
            System.err.println("Error: " + e.getMessage());
        }
    }
}
