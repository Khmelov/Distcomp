package com.distcomp.validator.note;

import com.distcomp.data.repository.note.NoteReactiveRepository;
import com.distcomp.dto.note.NoteCreateRequest;
import com.distcomp.dto.note.NoteUpdateRequest;
import com.distcomp.errorhandling.exceptions.BusinessValidationException;
import com.distcomp.validator.abstraction.BaseValidator;
import com.distcomp.validator.model.ValidationArgs;
import com.distcomp.validator.model.ValidationResult;
import com.distcomp.validator.topic.TopicValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class NoteValidator extends BaseValidator<NoteCreateRequest, NoteUpdateRequest> {

    private final TopicValidator topicValidator;
    private final NoteReactiveRepository noteRepository;

    public Mono<Void> validateNoteExists(final Long id) {
        return checkNotNull(id, "id", "ID must not be null")
                .flatMap(r -> {
                    if (!r.isValid()) {
                        return Mono.error(new BusinessValidationException(r.errors()));
                    }
                    return checkEntityExists(noteRepository, id, "id", "Note not found with id: " + id);
                })
                .flatMap(r -> {
                    if (r.isValid()) {
                        return Mono.empty();
                    } else {
                        return Mono.error(new BusinessValidationException(r.errors()));
                    }
                });
    }

    private Mono<ValidationResult> checkNoteExists(final Long id) {
        if (id == null) {
            return Mono.just(ValidationResult.of("id", "ID must not be null"));
        }
        return checkEntityExists(noteRepository, id, "id", "Note not found with id: " + id);
    }

    @Override
    public Mono<ValidationResult> validateUpdate(final NoteUpdateRequest request, final ValidationArgs args) {
        final Long id = args.id();

        Mono<ValidationResult> result = Mono.just(ValidationResult.ok());

        result = result.flatMap(r -> checkNotNull(id, "id", "ID must not be null")
                .map(r::merge));

        result = result.flatMap(r -> {
            if (id == null) {
                return Mono.just(r);
            }
            return checkNoteExists(id).map(r::merge);
        });

        return result.flatMap(r -> {
            if (r.isValid()) {
                return Mono.just(r);
            } else {
                return Mono.error(new BusinessValidationException(r.errors()));
            }
        });
    }

    @Override
    public Mono<ValidationResult> validateCreate(final NoteCreateRequest request, final ValidationArgs args) {
        final Long topicId = request.getTopicId();

        Mono<ValidationResult> result = Mono.just(ValidationResult.ok());

        result = result.flatMap(r -> checkNotNull(topicId, "topicId", "Topic ID must not be null")
                .map(r::merge));

        result = result.flatMap(r -> {
            if (topicId == null) {
                return Mono.just(r);
            }
            return topicValidator.checkTopicExists(topicId).map(r::merge);
        });

        return result.flatMap(r -> {
            if (r.isValid()) {
                return Mono.just(r);
            } else {
                return Mono.error(new BusinessValidationException(r.errors()));
            }
        });
    }
}