package reporting;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class EventsConsumer {

    private final ObjectMapper objectMapper;
    private final StatsStore statsStore;
    private static final Logger log = LoggerFactory.getLogger(EventsConsumer.class);


    public EventsConsumer(ObjectMapper objectMapper, StatsStore statsStore) {
        this.objectMapper = objectMapper;
        this.statsStore = statsStore;
    }

    @KafkaListener(
            topics = "${tictactoe.kafka.topic:tictactoe.events}",
            groupId = "${tictactoe.kafka.groupId:tictactoe-reporting}"
    )
    public void onMessage(String message) {
        log.info("Kafka message received: {}", message);
        try {
            EventEnvelope event = objectMapper.readValue(message, EventEnvelope.class);
            statsStore.handle(event);

        } catch (Exception e) {
            log.error("Cannot parse Kafka message. Raw: {}", message, e);
        }
    }
}
