package com.distcomp.validator.abstraction;

import com.distcomp.validator.model.ValidationArgs;
import com.distcomp.validator.model.ValidationResult;
import reactor.core.publisher.Mono;

public interface Validator<C, U> {

    Mono<ValidationResult> validateCreate(C createRequest, ValidationArgs args);

    Mono<ValidationResult> validateUpdate(U updateRequest, ValidationArgs args);
}