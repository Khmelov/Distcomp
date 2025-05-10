//package com.example.rest.kafka.produser;
//
//import com.example.rest.entity.Message;
//import com.example.rest.kafka.CrudEnum;
//import com.example.rest.kafka.KafkaEvent;
//import lombok.RequiredArgsConstructor;
//import org.springframework.kafka.core.KafkaTemplate;
//import org.springframework.stereotype.Service;
//
//@Service
//@RequiredArgsConstructor
//public class MessageProducer {
//    private final KafkaTemplate<String, KafkaEvent> kafkaTemplate;
//
//    public void sendMessage(CrudEnum eventType, Message message) {
//        String key = String.valueOf(message.getStoryId());
//        KafkaEvent event = new KafkaEvent(eventType, message);
//        kafkaTemplate.send("InTopic", key, event);
//    }
//
//}
