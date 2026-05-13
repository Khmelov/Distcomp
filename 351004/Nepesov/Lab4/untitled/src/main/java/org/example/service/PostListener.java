package org.example.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.PostMessage;
import org.example.model.Post;
import org.example.model.PostState;
import org.example.repository.PostRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@Profile("publisher")
@RequiredArgsConstructor
public class PostListener {

    private final PostRepository postRepository;
    private final KafkaTemplate<String, PostMessage> kafkaTemplate;

    @KafkaListener(topics = "InTopic", groupId = "discussion-group")
    public void listen(PostMessage message) { // Теперь void, чтобы Spring не искал ReplyTemplate
        log.info("Discussion: Received message for ID: {}", message.getId());

        try {
            // 1. Логика поиска/обработки
            if (message.getContent() == null) {
                Optional<Post> postOpt = postRepository.findById(message.getId());
                if (postOpt.isPresent()) {
                    Post p = postOpt.get();
                    message = new PostMessage(p.getId(), p.getNewsId(), p.getContent(), p.getState());
                }
            } else {
                // Логика спама
                if (message.getContent().toLowerCase().contains("spam")) {
                    message.setState(PostState.DECLINE);
                } else {
                    message.setState(PostState.APPROVE);
                }

                // Сохранение
                Post post = new Post();
                post.setId(message.getId());
                post.setNewsId(message.getNewsId());
                post.setContent(message.getContent());
                post.setState(message.getState());
                postRepository.save(post);
                log.info("Discussion: Saved to Cassandra with state: {}", post.getState());
            }

            // 2. ОТПРАВЛЯЕМ ОТВЕТ ВРУЧНУЮ
            kafkaTemplate.send("OutTopic", String.valueOf(message.getId()), message);
            log.info("Discussion: Reply manually sent to OutTopic for ID: {}", message.getId());

        } catch (Exception e) {
            log.error("Discussion: Error processing message: {}", e.getMessage());
        }
    }
}