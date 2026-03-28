package com.distcomp.validator.tag;

import com.distcomp.data.repository.tag.TagReactiveRepository;
import com.distcomp.dto.tag.TagCreateRequest;
import com.distcomp.dto.tag.TagUpdateRequest;
import com.distcomp.errorhandling.exceptions.BusinessValidationException;
import com.distcomp.errorhandling.exceptions.TagNotFoundException;
import com.distcomp.validator.abstraction.BaseValidator;
import com.distcomp.validator.model.ValidationArgs;
import com.distcomp.validator.model.ValidationResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class TagValidator extends BaseValidator<TagCreateRequest, TagUpdateRequest> {

    private final TagReactiveRepository tagRepository;

    public Mono<Void> validateTagExists(final Long id) {
        return assertIdNotNull(id).then(assertEntityExists(tagRepository, id,
                (Long missingId) -> new TagNotFoundException(
                        ValidationResult.of("id", "Tag not found with id: " + missingId).errors()
                )));
    }

    @Override
    public Mono<ValidationResult> validateUpdate(final TagUpdateRequest request, final ValidationArgs args) {
        final Long id = args.id();

        return assertIdNotNull(id)
                .then(checkEntityExists(tagRepository, id, "id", "Tag not found with id: " + id)
                        .flatMap((ValidationResult result) -> result.isValid() ?
                                Mono.just(ValidationResult.ok()) :
                                Mono.error(new TagNotFoundException(result.errors()))
                        )
                );
    }

    @Override
    public Mono<ValidationResult> validateCreate(final TagCreateRequest request, final ValidationArgs args) {
        return Mono.just(ValidationResult.ok());
    }

}