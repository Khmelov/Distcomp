package by.bsuir.dc.lab4.kafka;

import by.bsuir.dc.lab4.dto.CommentRequestTo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Exchanger;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Component
public class KafkaPublisherMessaging {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    private static ConcurrentHashMap<UUID, Exchanger<KafkaRequest>> cache = new ConcurrentHashMap<>();
    private static final String REQUEST_TOPIC = "IN_TOPIC";
    private static final String RESPONSE_TOPIC = "OUT_TOPIC";
    private ObjectMapper json = new JsonMapper();

    public List<CommentRequestTo> sendMessage(KafkaRequest request) throws JsonProcessingException{
            Exchanger<KafkaRequest> exchanger = new Exchanger<>();
            cache.put(request.getKey(),exchanger);
            String requestString = json.writeValueAsString(request);
            ProducerRecord<String,String> record = new ProducerRecord<>(REQUEST_TOPIC,request.getKey().toString(),requestString);
            try {
                kafkaTemplate.send(record);
                KafkaRequest resultRequest = exchanger.exchange(request, 1, TimeUnit.SECONDS);
                return resultRequest.getDtoToTransfer();
            }catch (TimeoutException | InterruptedException e){
                System.out.println("Error in message transfer to \"publisher\": timeout for response is over");
                return new ArrayList<>();
            }
    }

    @KafkaListener(topics = RESPONSE_TOPIC, groupId = "group1")
    void listener(ConsumerRecord<String,String> record) {

        UUID uuid = UUID.fromString(record.key());
        try {
            KafkaRequest request = json.readValue(record.value(), KafkaRequest.class);
            Exchanger<KafkaRequest> exchanger = cache.remove(uuid);
            if (exchanger != null) {
                try {
                    exchanger.exchange(request, 1, TimeUnit.SECONDS);
                } catch (TimeoutException | InterruptedException e) {
                    System.out.println("Error in message transfer to \"publisher\": timeout for response is over");
                }
            }
        }catch (JsonProcessingException e){
            return;
        }
    }
}
