package by.egorsosnovski.distcomp.kafka;


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
    @SneakyThrows
    public MessageData send(MessageData messageToSend) throws TimeoutException {
        UUID uuid = UUID.randomUUID();
        Exchanger<MessageData> exchanger = new Exchanger<>();
        kafkaCache.put(uuid, exchanger);
        try {
            sender.send(REQUEST_TOPIC, uuid.toString(), mapper.writeValueAsString(messageToSend));
            MessageData messageRecieved = exchanger.exchange(messageToSend, 1, TimeUnit.SECONDS);
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
    @SneakyThrows
    @KafkaListener(topics = RESPONSE_TOPIC, groupId = "groupId =#{T(java.util.UUID).randomUUID().toString()}")
    private void getMessage(ConsumerRecord<String, String> record){
        UUID uuid = UUID.fromString(record.key());
        MessageData message = mapper.readValue(record.value(), MessageData.class);
        Exchanger<MessageData> exchanger = kafkaCache.remove(uuid);
        if(exchanger != null){
            exchanger.exchange(message, 1, TimeUnit.SECONDS);
        }
    }
}
