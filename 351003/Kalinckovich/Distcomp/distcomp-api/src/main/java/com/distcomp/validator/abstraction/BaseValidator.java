package com.distcomp.validator.abstraction;


import com.distcomp.errorhandling.exceptions.BusinessValidationException;
import com.distcomp.validator.model.ValidationResult;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

import java.util.function.Function;

public abstract class BaseValidator<C, U> implements Validator<C, U> {

    protected Mono<ValidationResult> checkNotNull(final Object value, final String fieldName, final String message) {
        if (value == null) {
            return Mono.just(ValidationResult.of(fieldName, message));
        }
        return Mono.just(ValidationResult.ok());
    }

    protected Mono<ValidationResult> assertIdNotNull(final Long id) {
        if (id == null) {
            return Mono.error(new BusinessValidationException(
                    ValidationResult.of("id", "ID must not be null").errors()
            ));
        }
        return Mono.just(ValidationResult.ok());
    }

    protected <ID, T> Mono<Void> assertEntityExists(
            final ReactiveCrudRepository<T, ID> repository,
            final ID id,
            final Function<ID, ? extends Throwable> exceptionSupplier) {

        return repository.existsById(id)
                .flatMap(exists -> exists
                        ? Mono.empty()
                        : Mono.error(exceptionSupplier.apply(id)));
    }

    
    protected <ID> Mono<ValidationResult> checkEntityExists(
            final ReactiveCrudRepository<?, ID> repository,
            final ID id,
            final String fieldName,
            final String errorMessage) {

        return repository.existsById(id)
                .map(exists -> exists
                        ? ValidationResult.ok()
                        : ValidationResult.of(fieldName, errorMessage));
    }

    
    protected Mono<ValidationResult> checkEntityExists(
            final Mono<Boolean> existenceCheck,
            final String fieldName,
            final String errorMessage) {

        return existenceCheck
                .map(exists -> exists
                        ? ValidationResult.ok()
                        : ValidationResult.of(fieldName, errorMessage));
    }
}