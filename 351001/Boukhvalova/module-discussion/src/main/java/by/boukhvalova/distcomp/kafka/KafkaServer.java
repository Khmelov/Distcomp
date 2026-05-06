package by.boukhvalova.distcomp.kafka;


import by.boukhvalova.distcomp.controllers.NoteKafkaController;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.logging.Logger;

@EnableKafka
@Component
@RequiredArgsConstructor
public class KafkaServer {
    public static final String REQUEST_TOPIC = "InTopic";
    public static final String RESPONSE_TOPIC = "OutTopic";
    public final NoteKafkaController noteKafkaController;
    private final KafkaTemplate<String, String> sender;
    private final ObjectMapper mapper;

    @SneakyThrows
    @KafkaListener(topics = REQUEST_TOPIC, groupId = "discussion-service")
    private void getMessage(ConsumerRecord<String, String> record){
        MessageData message = mapper.readValue(record.value(), MessageData.class);
        String correlationId = message.correlationId() == null ? record.key() : message.correlationId();
        MessageData response = null;
        try {
            switch (message.operation()) {
                case GET_ALL -> response = new MessageData(message.operation(), noteKafkaController.getAll());
                case CREATE ->
                        response = new MessageData(message.operation(), Collections.singletonList(noteKafkaController.create(message.requestTO())));
                case UPDATE ->
                        response = new MessageData(message.operation(), Collections.singletonList(noteKafkaController.update(message.requestTO())));
                case GET_BY_ID ->
                        response = new MessageData(message.operation(), Collections.singletonList(noteKafkaController.getById(message.itemId())));
                case DELETE_BY_ID -> noteKafkaController.delete(message.itemId());
            }
            if(response == null){
                response = new MessageData(message.operation());
            }
        }catch (Exception e) {
            Logger.getAnonymousLogger().info(String.format("Caught exception:%s", e.getMessage()));
            response = new MessageData(new MessageData.ExceptionData(e.getClass().getSimpleName(),e.getMessage()));
        }
        MessageData routedResponse = response.withRouting(correlationId, correlationId);
        sender.send(RESPONSE_TOPIC, correlationId, mapper.writeValueAsString(routedResponse));
    }
}
