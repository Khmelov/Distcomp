package by.shaminko.distcomp.kafka;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Exchanger;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@EnableKafka
@Component
@RequiredArgsConstructor
public class KafkaClient {
    public static final String REQUEST_TOPIC = "InTopic";
    public static final String RESPONSE_TOPIC = "OutTopic";
    private final KafkaTemplate<String, String> sender;
    private final ObjectMapper mapper;
    private final ConcurrentHashMap<UUID, Exchanger<MessageData>> kafkaCache = new ConcurrentHashMap<>();

    public MessageData send(MessageData messageToSend) throws TimeoutException {
        UUID uuid = UUID.randomUUID();
        Exchanger<MessageData> exchanger = new Exchanger<>();
        kafkaCache.put(uuid, exchanger);
        MessageData routedMessage = messageToSend.withRouting(uuid.toString(), resolvePartitionKey(messageToSend, uuid.toString()));
        try {
            sender.send(REQUEST_TOPIC, routedMessage.partitionKey(), mapper.writeValueAsString(routedMessage));
            MessageData messageRecieved = exchanger.exchange(routedMessage, 1000, TimeUnit.MILLISECONDS);
            if(messageRecieved.operation() != MessageData.Operation.EXCEPTION){
                kafkaCache.remove(uuid, exchanger);
                return messageRecieved;
            }else{
                throw new KafkaException(messageRecieved.exception().simpleName(), messageRecieved.exception().message());
            }

        }catch (JsonProcessingException | InterruptedException e){
            throw new RuntimeException(e);
        }catch (Exception e){
            kafkaCache.remove(uuid, exchanger);
            throw e;
        }
    }

    @KafkaListener(topics = RESPONSE_TOPIC, groupId = "publisher-response-group")
    private void getMessage(ConsumerRecord<String, String> record) {
        try {
            UUID uuid = UUID.fromString(record.key());
            MessageData message = mapper.readValue(record.value(), MessageData.class);

            Exchanger<MessageData> exchanger = kafkaCache.remove(uuid);
            if(exchanger != null){
                exchanger.exchange(message, 100, TimeUnit.MILLISECONDS);
            }
        } catch (Exception e) {
        }
    }

    private String resolvePartitionKey(MessageData messageToSend, String fallbackKey) {
        if (messageToSend.requestTO() != null && messageToSend.requestTO().getArticleId() > 0) {
            return String.valueOf(messageToSend.requestTO().getArticleId());
        }
        if (messageToSend.itemId() != null) {
            return String.valueOf(messageToSend.itemId());
        }
        return fallbackKey;
    }
}
