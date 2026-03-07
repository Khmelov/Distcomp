package com.distcomp.validation.note;

import com.distcomp.data.repository.note.NoteReactiveRepository;
import com.distcomp.dto.note.NoteCreateRequest;
import com.distcomp.dto.note.NoteUpdateRequest;
import com.distcomp.errorhandling.exceptions.BusinessValidationException;
import com.distcomp.model.note.Note;
import com.distcomp.validation.abstraction.BaseValidator;
import com.distcomp.validation.model.ValidationArgs;
import com.distcomp.validation.model.ValidationResult;
import com.distcomp.validator.topic.TopicValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class NoteValidator extends BaseValidator<NoteCreateRequest, NoteUpdateRequest> {

    private final TopicValidator topicValidator;
    private final NoteReactiveRepository noteRepository;

    /**
     * Validate that a note exists by its composite key (topicId + noteId).
     * Returns Mono.empty() if valid, otherwise Mono.error with BusinessValidationException.
     */
    public Mono<Void> validateNoteExists(final Long topicId, final UUID noteId) {
        return checkNotNull(topicId, "topicId", "Topic ID must not be null")
                .flatMap(r -> {
                    if (!r.isValid()) {
                        return Mono.error(new BusinessValidationException(r.errors()));
                    }
                    return checkNotNull(noteId, "noteId", "Note ID must not be null");
                })
                .flatMap(r -> {
                    if (!r.isValid()) {
                        return Mono.error(new BusinessValidationException(r.errors()));
                    }
                    final Note.NoteKey key = new Note.NoteKey(topicId, noteId);
                    return checkEntityExists(noteRepository, key, "note",
                            "Note not found with topicId: " + topicId + " and id: " + noteId);
                })
                .flatMap(r -> r.isValid() ? Mono.empty() : Mono.error(new BusinessValidationException(r.errors())));
    }

    @Override
    public Mono<ValidationResult> validateUpdate(final NoteUpdateRequest request, final ValidationArgs args) {
        final Long topicId = args.id() != null ? args.id() : null;
        final UUID noteId = args.extras() != null ? (UUID) args.extras().get("noteId") : null;

        Mono<ValidationResult> result = Mono.just(ValidationResult.ok());

        result = result.flatMap(r -> checkNotNull(topicId, "topicId", "Topic ID must not be null").map(r::merge));
        result = result.flatMap(r -> checkNotNull(noteId, "noteId", "Note ID must not be null").map(r::merge));

        result = result.flatMap(r -> {
            if (topicId != null && noteId != null) {
                final Note.NoteKey key = new Note.NoteKey(topicId, noteId);
                return checkEntityExists(noteRepository, key, "note", "Note not found").map(r::merge);
            }
            return Mono.just(r);
        });

        return result.flatMap(r -> r.isValid() ? Mono.just(r) : Mono.error(new BusinessValidationException(r.errors())));
    }

    @Override
    public Mono<ValidationResult> validateCreate(final NoteCreateRequest request, final ValidationArgs args) {
        final Long topicId = request.getTopicId();

        Mono<ValidationResult> result = Mono.just(ValidationResult.ok());

        result = result.flatMap(r -> checkNotNull(topicId, "topicId", "Topic ID must not be null").map(r::merge));

        result = result.flatMap(r -> {
            if (topicId == null) {
                return Mono.just(r);
            }
            return topicValidator.checkTopicExists(topicId).map(r::merge);
        });

        return result.flatMap(r -> r.isValid() ? Mono.just(r) : Mono.error(new BusinessValidationException(r.errors())));
    }
}