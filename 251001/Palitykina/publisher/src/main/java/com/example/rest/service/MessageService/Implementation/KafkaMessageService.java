package com.example.rest.service.MessageService.Implementation;

import com.example.rest.dto.requestDto.MessageRequestTo;
import com.example.rest.dto.responseDto.MessageResponseTo;
import com.example.rest.dto.updateDto.MessageUpdateTo;
//import com.example.rest.kafka.produser.MessageProducer;
import com.example.rest.repository.StoryRepo;
import com.example.rest.service.MessageService.MessageService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class KafkaMessageService implements MessageService {
    //private final MessageProducer messageProducer;
    private final StoryRepo storyRepo;

    public List<MessageResponseTo> getAll() {
        return List.of();
    }


    public MessageResponseTo get(long id) {
        return null;
    }

    public MessageResponseTo create(MessageRequestTo input) {
        return null;
    }

    public MessageResponseTo update(MessageUpdateTo input) {
        return null;
    }

    public void delete(long id) {

    }




}
