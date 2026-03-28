package com.distcomp.validation.abstraction;

import com.distcomp.validation.model.ValidationArgs;
import com.distcomp.validation.model.ValidationResult;
import reactor.core.publisher.Mono;

public interface Validator<C, U> {

    Mono<ValidationResult> validateCreate(C createRequest, ValidationArgs args);

    Mono<ValidationResult> validateUpdate(U updateRequest, ValidationArgs args);
}