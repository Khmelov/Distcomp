package by.yelkin.TopicService.service;

import by.yelkin.TopicService.dto.topic.TopicRq;
import by.yelkin.TopicService.dto.topic.TopicRs;
import by.yelkin.TopicService.dto.topic.TopicUpdateRq;
import by.yelkin.TopicService.exception.ApiError;
import by.yelkin.TopicService.exception.ApiException;
import by.yelkin.TopicService.mapping.TopicMapper;
import by.yelkin.TopicService.repository.CreatorRepository;
import by.yelkin.TopicService.repository.TopicRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TopicService {
    private final TopicRepository topicRepository;
    private final CreatorRepository creatorRepository;
    private final TopicMapper topicMapper;

    public TopicRs create(TopicRq rq) {
        if (creatorRepository.findById(rq.getCreatorId()).isEmpty()) {
            throw new ApiException(ApiError.ERR_CREATOR_NOT_FOUND, rq.getCreatorId().toString());
        }
        return topicMapper.toDto(topicRepository.save(topicMapper.fromDto(rq)));
    }

    public TopicRs readById(Long id) {
        return topicMapper.toDto(topicRepository.findById(id)
                .orElseThrow(() -> new ApiException(ApiError.ERR_TOPIC_NOT_FOUND, id.toString())));
    }

    public List<TopicRs> readAll() {
        return topicMapper.toDtoList(topicRepository.findAll());
    }

    public TopicRs update(TopicUpdateRq rq) {
        var topic = topicRepository.findById(rq.getId())
                .orElseThrow(() -> new ApiException(ApiError.ERR_TOPIC_NOT_FOUND, rq.getId().toString()));

        topicMapper.updateTopic(topic, rq);

        return topicMapper.toDto(topicRepository.save(topic));
    }

    public void deleteById(Long id) {
        if (topicRepository.findById(id).isEmpty()) {
            throw new ApiException(ApiError.ERR_TOPIC_NOT_FOUND, id.toString());
        }
        topicRepository.deleteById(id);
    }
}
