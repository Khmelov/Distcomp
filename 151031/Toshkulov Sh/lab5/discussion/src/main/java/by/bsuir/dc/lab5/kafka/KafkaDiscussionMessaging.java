package by.bsuir.dc.lab5.kafka;

import by.bsuir.dc.lab5.dto.mappers.CommentMapper;
import by.bsuir.dc.lab5.entities.Comment;
import by.bsuir.dc.lab5.services.interfaces.CommentDiscService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class KafkaDiscussionMessaging {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private CommentDiscService commentService;

    private static final String REQUEST_TOPIC = "IN_TOPIC";
    private static final String RESPONSE_TOPIC = "OUT_TOPIC";

    private ObjectMapper json = new JsonMapper();

    public void sendMessage(KafkaRequest request) throws JsonProcessingException {
        String requestString = json.writeValueAsString(request);
        ProducerRecord<String, String> record = new ProducerRecord<>(RESPONSE_TOPIC, request.getKey().toString(), requestString);
        kafkaTemplate.send(record);
    }


    @KafkaListener(topics = REQUEST_TOPIC, groupId = "group1")
    void listener(ConsumerRecord<String, String> record) {
        try {
            KafkaRequest request = json.readValue(record.value(), KafkaRequest.class);
            Methods method = request.getRequestMethod();
            switch (method) {
                case GET_BY_ID -> {
                    Comment comment = commentService.getById(request.getDtoToTransfer().get(0).getId());
                    request.getDtoToTransfer().clear();
                    if (comment != null) {
                        request.getDtoToTransfer().add(CommentMapper.instance.convertCommentToRequest(comment));
                    }
                    try {
                        sendMessage(request);
                    } catch (JsonProcessingException e) {
                        return;
                    }
                }
                case GET_ALL -> {
                    List<Comment> comments = commentService.getAll();
                    request.getDtoToTransfer().clear();
                    if (comments != null) {
                        for (Comment comment : comments) {
                            request.getDtoToTransfer().add(CommentMapper.instance.convertCommentToRequest(comment));
                        }
                    }
                    try {
                        sendMessage(request);
                    } catch (JsonProcessingException e) {
                        return;
                    }
                }
                case CREATE -> {
                    Comment comment = commentService.add(CommentMapper.instance.convertFromDTO(request.getDtoToTransfer().get(0)));
                    request.getDtoToTransfer().clear();
                    if (comment != null) {
                        request.getDtoToTransfer().add(CommentMapper.instance.convertCommentToRequest(comment));
                    }
                    try {
                        sendMessage(request);
                    } catch (JsonProcessingException e) {
                        return;
                    }
                }
                case DELETE -> {
                    commentService.delete(request.getDtoToTransfer().get(0).getId());
                    request.getDtoToTransfer().clear();
                    sendMessage(request);
                }
                case UPDATE -> {
                    Comment comment = commentService.update(CommentMapper.instance.convertFromDTO(request.getDtoToTransfer().get(0)));
                    request.getDtoToTransfer().clear();
                    if (comment != null) {
                        request.getDtoToTransfer().add(CommentMapper.instance.convertCommentToRequest(comment));
                    }
                    try {
                        sendMessage(request);
                    } catch (JsonProcessingException e) {
                        return;
                    }
                }
            }
        } catch (JsonProcessingException e) {
            return;
        }
    }
}
