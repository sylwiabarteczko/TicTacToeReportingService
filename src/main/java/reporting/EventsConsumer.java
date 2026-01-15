package reporting;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class EventsConsumer {

    @KafkaListener(
            topics = "${tictactoe.kafka.topic:tictactoe.events}",
            groupId = "${tictactoe.kafka.groupId:tictactoe-reporting}"
    )
    public void onMessage(String message) {
        System.out.println("EVENT from Kafka: " + message);
    }
}
