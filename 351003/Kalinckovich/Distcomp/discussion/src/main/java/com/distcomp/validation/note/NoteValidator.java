package com.distcomp.validation.note;

import com.distcomp.errorhandling.model.ValidationError;
import com.distcomp.repository.cassandra.NoteCassandraReactiveRepository;
import com.distcomp.dto.note.NoteCreateRequest;
import com.distcomp.dto.note.NoteUpdateRequest;
import com.distcomp.errorhandling.exceptions.NoteNotFoundException;
import com.distcomp.model.note.Note;
import com.distcomp.validation.abstraction.BaseValidator;
import com.distcomp.validation.model.ValidationArgs;
import com.distcomp.validation.model.ValidationResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Component("cassandraNoteValidator")
@RequiredArgsConstructor
public class NoteValidator extends BaseValidator<NoteCreateRequest, NoteUpdateRequest> {

    private static final String DEFAULT_COUNTRY = "default";

    private final NoteCassandraReactiveRepository noteRepository;

    /**
     * Validates that a note exists for the given composite key (country, topicId, noteId).
     */
    public Mono<Void> validateNoteExists(Long topicId, Long noteId) {
        Note.NoteKey key = new Note.NoteKey(DEFAULT_COUNTRY, topicId, noteId);
        return noteRepository.existsById(key)
                .flatMap(exists -> {
                    if (!exists) {
                        List<ValidationError> errors = List.of(
                                new ValidationError("note", "Note not found with topicId: " + topicId + " and id: " + noteId)
                        );
                        return Mono.error(new NoteNotFoundException(errors));
                    }
                    return Mono.empty();
                });
    }

    /**
     * Validates that a note exists for the given single ID (using the materialized view).
     */
    public Mono<Void> validateNoteExists(Long id) {
        return noteRepository.findByNoteId(id)  
                .hasElement()
                .flatMap(exists -> {
                    if (!exists) {
                        List<ValidationError> errors = List.of(
                                new ValidationError("note", "Note not found with id: " + id)
                        );
                        return Mono.error(new NoteNotFoundException(errors));
                    }
                    return Mono.empty();
                });
    }

    @Override
    public Mono<ValidationResult> validateUpdate(NoteUpdateRequest request, ValidationArgs args) {
        Long topicId = args.id() != null ? args.id() : null;
        Long noteId = args.extras() != null ? (Long) args.extras().get("noteId") : null;

        Mono<ValidationResult> result = Mono.just(ValidationResult.ok());

        result = result.flatMap(r -> checkNotNull(topicId, "topicId", "Topic ID must not be null").map(r::merge));
        result = result.flatMap(r -> checkNotNull(noteId, "noteId", "Note ID must not be null").map(r::merge));

        result = result.flatMap(r -> {
            if (topicId != null && noteId != null) {
                Note.NoteKey key = new Note.NoteKey(DEFAULT_COUNTRY, topicId, noteId);
                return checkEntityExists(noteRepository, key, "note", "Note not found").map(r::merge);
            }
            return Mono.just(r);
        });

        return result.flatMap(r -> r.isValid() ? Mono.just(r) : Mono.error(new NoteNotFoundException(r.errors())));
    }

    @Override
    public Mono<ValidationResult> validateCreate(NoteCreateRequest request, ValidationArgs args) {
        Long topicId = request.getTopicId();

        Mono<ValidationResult> result = Mono.just(ValidationResult.ok());

        result = result.flatMap(r -> checkNotNull(topicId, "topicId", "Topic ID must not be null").map(r::merge));

        
        return result.flatMap(r -> r.isValid() ? Mono.just(r) : Mono.error(new NoteNotFoundException(r.errors())));
    }
}