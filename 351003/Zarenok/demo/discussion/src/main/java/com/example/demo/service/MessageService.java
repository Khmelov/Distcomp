package com.example.demo.service;

import com.example.demo.dto.requests.MessageKafkaRequestTo;
import com.example.demo.dto.requests.MessageRequestTo;
import com.example.demo.dto.responses.MessageResponseTo;
import com.example.demo.exception.NotFoundException;
import com.example.demo.model.Message;
import com.example.demo.model.MessageKey;
import com.example.demo.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.cassandra.core.CassandraTemplate;
import org.springframework.data.cassandra.core.query.Criteria;
import org.springframework.data.cassandra.core.query.Query;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository repository;
    private final MessageMapper mapper;
    private final CassandraTemplate cassandraTemplate;

    // CREATE
    public MessageResponseTo create(MessageRequestTo dto) {
        Long generatedId = System.nanoTime();
        MessageKey key = new MessageKey(dto.getIssueId(), generatedId);
        Message entity = new Message();
        entity.setKey(key);
        entity.setContent(dto.getContent());
        Message saved = repository.save(entity);
        return mapper.toResponse(saved);
    }


    // READ by id only
    public MessageResponseTo findByIdOnly(Long id) {
        Query query = Query.query(Criteria.where("id").is(id)).withAllowFiltering();
        Message message = cassandraTemplate.selectOne(query, Message.class);
        if (message == null) throw new NotFoundException("Message not found");
        return mapper.toResponse(message);
    }

    // READ by issueId + id
    public MessageResponseTo findById(Long issueId, Long id) {
        MessageKey key = new MessageKey(issueId, id);
        Message message = repository.findById(key)
                .orElseThrow(() -> new NotFoundException("Message not found with issueId=" + issueId + ", id=" + id));
        return mapper.toResponse(message);
    }

    // READ all messages for a specific issue
    public List<MessageResponseTo> findAllByIssueId(Long issueId) {
        return repository.findByKeyIssueId(issueId).stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    public Page<MessageResponseTo> findAll(Pageable pageable, Long issueId, String content) {
        List<Message> messages;

        if (issueId != null) {
            messages = repository.findByKeyIssueId(issueId);
        } else {
            messages = cassandraTemplate.select(Query.empty(), Message.class);
        }

        if (content != null && !content.isEmpty()) {
            messages = messages.stream()
                    .filter(m -> m.getContent() != null && m.getContent().contains(content))
                    .collect(Collectors.toList());
        }
        if (pageable.getSort().isSorted()) {
            messages.sort((a, b) -> {
                for (Sort.Order order : pageable.getSort()) {
                    int cmp = compareWithOrder(a, b, order);
                    if (cmp != 0) return cmp;
                }
                return 0;
            });
        }

        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), messages.size());
        List<MessageResponseTo> pageContent = messages.subList(start, end).stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());

        return new PageImpl<>(pageContent, pageable, messages.size());
    }

    private int compareWithOrder(Message a, Message b, Sort.Order order) {
        String prop = order.getProperty();
        int cmp = 0;
        switch (prop) {
            case "id":
                cmp = a.getKey().getId().compareTo(b.getKey().getId());
                break;
            case "issueId":
                cmp = a.getKey().getIssueId().compareTo(b.getKey().getIssueId());
                break;
            case "content":
                cmp = a.getContent().compareTo(b.getContent());
                break;
            default:
                cmp = 0;
        }
        return order.isAscending() ? cmp : -cmp;
    }

    public List<MessageResponseTo> findAll(String content, Long issueId) {
        List<Message> messages;
        if (issueId != null) {
            messages = repository.findByKeyIssueId(issueId);
        } else {
            messages = cassandraTemplate.select(Query.empty(), Message.class);
        }
        if (content != null && !content.isEmpty()) {
            messages = messages.stream()
                    .filter(m -> m.getContent() != null && m.getContent().contains(content))
                    .collect(Collectors.toList());
        }
        messages.sort(Comparator.comparing(m -> m.getKey().getId()));
        return messages.stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    // UPDATE
    public MessageResponseTo update(Long issueId, Long id, MessageRequestTo dto) {
        MessageKey key = new MessageKey(issueId, id);
        Message existing = repository.findById(key)
                .orElseThrow(() -> new NotFoundException("Message not found with issueId=" + issueId + ", id=" + id));

        mapper.updateEntity(dto, existing);
        existing.setKey(key);
        Message updated = repository.save(existing);
        return mapper.toResponse(updated);
    }

    // DELETE
    public void delete(Long issueId, Long id) {
        MessageKey key = new MessageKey(issueId, id);
        if (!repository.existsById(key)) {
            throw new NotFoundException("Message not found with issueId=" + issueId + ", id=" + id);
        }
        repository.deleteById(key);
    }

    public MessageResponseTo saveFromKafka(MessageKafkaRequestTo kafkaRequest) {
        MessageKey key = new MessageKey(kafkaRequest.getIssueId(), kafkaRequest.getId());

        Message entity = new Message();
        entity.setKey(key);
        entity.setContent(kafkaRequest.getContent());
        entity.setState("PENDING");

        Message saved = repository.save(entity);
        return mapper.toResponse(saved);
    }

    public MessageResponseTo updateState(Long issueId, Long id, String newState) {
        MessageKey key = new MessageKey(issueId, id);
        Message message = repository.findById(key)
                .orElseThrow(() -> new NotFoundException("Message not found"));
        message.setState(newState);
        Message updated = repository.save(message);
        return mapper.toResponse(updated);
    }
}
