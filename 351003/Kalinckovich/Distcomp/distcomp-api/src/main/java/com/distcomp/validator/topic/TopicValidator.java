package com.distcomp.validator.topic;

import com.distcomp.data.repository.topic.TopicReactiveRepository;
import com.distcomp.dto.topic.TopicCreateRequest;
import com.distcomp.dto.topic.TopicUpdateRequest;
import com.distcomp.errorhandling.exceptions.BusinessValidationException;
import com.distcomp.validator.abstraction.BaseValidator;
import com.distcomp.validator.model.ValidationArgs;
import com.distcomp.validator.model.ValidationResult;
import com.distcomp.validator.user.UserValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class TopicValidator extends BaseValidator<TopicCreateRequest, TopicUpdateRequest> {

    private final TopicReactiveRepository topicRepository;
    private final UserValidator userValidator;

    private Mono<ValidationResult> checkTitleUnique(final String title, final Long excludeTopicId) {
        if (title == null || title.isBlank()) {
            return Mono.just(ValidationResult.of("title", "Title must not be empty"));
        }
        return topicRepository.findByTitle(title)
                .flatMap(existingTopic -> {
                    if (existingTopic.getId().equals(excludeTopicId)) {
                        return Mono.just(ValidationResult.ok());
                    } else {
                        return Mono.just(ValidationResult.of("title", "Title already exists"));
                    }
                })
                .switchIfEmpty(Mono.just(ValidationResult.ok()));
    }

    @Override
    public Mono<ValidationResult> validateUpdate(final TopicUpdateRequest request, final ValidationArgs args) {
        final Long topicId = args.id();
        final Long userId = request.getUserId();
        final String title = request.getTitle();

        Mono<ValidationResult> result = Mono.just(ValidationResult.ok());

        
        result = result.flatMap(r -> checkNotNull(topicId, "id", "ID must not be null")
                .map(r::merge));

        
        result = result.flatMap(r -> {
            if (topicId == null) {
                return Mono.just(r);
            }
            return checkEntityExists(topicRepository, topicId, "id", "Topic not found with id: " + topicId)
                    .map(r::merge);
        });

        
        if (title != null && !title.isBlank()) {
            result = result.flatMap(r -> checkTitleUnique(title, topicId)
                    .map(r::merge));
        }

        
        result = validateUser(userId, result);

        return result;
    }

    @Override
    public Mono<ValidationResult> validateCreate(final TopicCreateRequest request, final ValidationArgs args) {
        final Long userId = request.getUserId();
        final String title = request.getTitle();

        Mono<ValidationResult> result = Mono.just(ValidationResult.ok());

        
        result = result.flatMap(r -> {
            if (title == null || title.isBlank()) {
                return Mono.just(r.merge(ValidationResult.of("title", "Title must not be empty")));
            }
            return Mono.just(r);
        });

        
        if (title != null && !title.isBlank()) {
            result = result.flatMap(r -> checkTitleUnique(title, null)
                    .map(r::merge));
        }

        
        result = validateUser(userId, result);

        return result;
    }

    private Mono<ValidationResult> validateUser(final Long userId, Mono<ValidationResult> result) {
        result = result.flatMap(r -> checkNotNull(userId, "userId", "User ID must not be null")
                .map(r::merge));

        result = result.flatMap(r -> {
            if (userId == null) {
                return Mono.just(r);
            }
            return userValidator.checkUserExists(userId)
                    .map(r::merge);
        });

        return result.flatMap(r -> {
            if (r.isValid()) {
                return Mono.just(r);
            } else {
                return Mono.error(new BusinessValidationException(r.errors()));
            }
        });
    }

    public Mono<Void> validateTopicExists(final Long id) {
        return checkNotNull(id, "id", "ID must not be null")
                .flatMap(r -> {
                    if (!r.isValid()) {
                        return Mono.error(new BusinessValidationException(r.errors()));
                    }
                    return checkEntityExists(topicRepository, id, "id", "Topic not found with id: " + id);
                })
                .flatMap(r -> {
                    if (r.isValid()) {
                        return Mono.empty();
                    } else {
                        return Mono.error(new BusinessValidationException(r.errors()));
                    }
                });
    }

    public Mono<ValidationResult> checkTopicExists(final Long topicId) {
        if (topicId == null) {
            return Mono.just(ValidationResult.of("topicId", "Topic ID must not be null"));
        }
        return checkEntityExists(topicRepository, topicId, "topicId", "Topic not found with id: " + topicId);
    }
}