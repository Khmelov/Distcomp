package com.task310.discussion.service.kafka;

import tools.jackson.databind.ObjectMapper;
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.PreparedStatement;
import com.task310.discussion.dto.kafka.PostKafkaRequest;
import com.task310.discussion.dto.kafka.PostKafkaResponse;
import com.task310.discussion.model.Post;
import com.task310.discussion.model.PostKey;
import com.task310.discussion.model.PostState;
import com.task310.discussion.repository.PostRepository;
import com.task310.discussion.service.ModerationService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class PostKafkaConsumer {

    private static final Logger logger = LoggerFactory.getLogger(PostKafkaConsumer.class);
    
    @Value("${spring.kafka.consumer.topic.in:InTopic}")
    private String inTopic;
    
    @Value("${spring.kafka.producer.topic.out:OutTopic}")
    private String outTopic;
    private static final AtomicLong idGenerator = new AtomicLong(System.currentTimeMillis());

    private final PostRepository postRepository;
    private final ModerationService moderationService;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final CqlSession cqlSession;
    private PreparedStatement insertStatement;

    @Autowired
    public PostKafkaConsumer(PostRepository postRepository,
                            ModerationService moderationService,
                            KafkaTemplate<String, Object> kafkaTemplate,
                            @Qualifier("kafkaObjectMapper") ObjectMapper objectMapper,
                            CqlSession cqlSession) {
        this.postRepository = postRepository;
        this.moderationService = moderationService;
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
        this.cqlSession = cqlSession;
        logger.info("PostKafkaConsumer constructor called");
    }
    
    @PostConstruct
    public void init() {
        logger.info("PostKafkaConsumer initialized with inTopic={}, outTopic={}", inTopic, outTopic);
        // Prepare INSERT statement
        String insertCql = "INSERT INTO distcomp.tbl_post (article_id, id, content, state, created, modified) VALUES (?, ?, ?, ?, ?, ?)";
        this.insertStatement = cqlSession.prepare(insertCql);
    }

    @KafkaListener(topics = "${spring.kafka.consumer.topic.in:InTopic}", groupId = "${spring.kafka.consumer.group-id:discussion-group}", containerFactory = "kafkaListenerContainerFactory")
    public void consume(@Payload Object messagePayload,
                       @Header(KafkaHeaders.RECEIVED_KEY) String key,
                       Acknowledgment acknowledgment) {
        logger.info("Received raw message from InTopic: payload type={}, key={}, payload={}", 
            messagePayload != null ? messagePayload.getClass().getName() : "null", key, messagePayload);
        try {
            // Extract value from ConsumerRecord if needed
            Object actualPayload = messagePayload;
            if (messagePayload instanceof ConsumerRecord) {
                ConsumerRecord<?, ?> record = (ConsumerRecord<?, ?>) messagePayload;
                actualPayload = record.value();
                logger.info("Extracted value from ConsumerRecord: {}", actualPayload);
            }
            
            // Deserialize Object (Map) to PostKafkaRequest
            PostKafkaRequest request;
            if (actualPayload instanceof PostKafkaRequest) {
                request = (PostKafkaRequest) actualPayload;
            } else {
                // Convert Map or other Object to PostKafkaRequest
                request = objectMapper.readValue(
                    objectMapper.writeValueAsString(actualPayload), 
                    PostKafkaRequest.class
                );
            }
            logger.info("Deserialized message from InTopic: articleId={}, content={}, id={}", 
                request.getArticleId(), request.getContent(), request.getId());

            // Generate ID if not provided
            Long postId = request.getId();
            if (postId == null || postId <= 0) {
                postId = idGenerator.incrementAndGet();
            }

            // Create Post with PENDING state initially
            PostKey postKey = new PostKey(request.getArticleId(), postId);
            Post post = new Post();
            post.setKey(postKey);
            post.setContent(request.getContent());
            post.setState(PostState.PENDING);
            
            LocalDateTime now = LocalDateTime.now();
            post.setCreated(now);
            post.setModified(now);

            // Perform moderation
            PostState moderationResult = moderationService.moderate(request.getContent());
            post.setState(moderationResult);

            // Save post using CqlSession to avoid StatementBuilder issue
            Instant createdInstant = post.getCreated().atZone(ZoneId.systemDefault()).toInstant();
            Instant modifiedInstant = post.getModified().atZone(ZoneId.systemDefault()).toInstant();
            
            cqlSession.execute(insertStatement.bind(
                post.getArticleId(),
                post.getId(),
                post.getContent(),
                post.getState().name(),
                createdInstant,
                modifiedInstant
            ));
            
            logger.info("Post saved with state: {}", post.getState());
            Post savedPost = post;

            // Send response to OutTopic
            PostKafkaResponse response = createResponse(savedPost);
            kafkaTemplate.send(outTopic, String.valueOf(savedPost.getArticleId()), response);
            logger.info("Sent response to OutTopic: id={}, state={}", response.getId(), response.getState());

            acknowledgment.acknowledge();
        } catch (Exception e) {
            logger.error("Error processing message from InTopic: {}", e.getMessage(), e);
            // Acknowledge even on error to avoid infinite retry loop
            acknowledgment.acknowledge();
        }
    }

    private PostKafkaResponse createResponse(Post post) {
        PostKafkaResponse response = new PostKafkaResponse();
        response.setId(post.getId());
        response.setArticleId(post.getArticleId());
        response.setContent(post.getContent());
        response.setState(post.getState());
        response.setCreated(post.getCreated());
        response.setModified(post.getModified());
        return response;
    }
}

