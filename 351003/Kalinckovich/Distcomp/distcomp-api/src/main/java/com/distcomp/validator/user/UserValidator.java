package com.distcomp.validator.user;

import com.distcomp.data.repository.user.UserReactiveRepository;
import com.distcomp.dto.user.UserCreateRequest;
import com.distcomp.dto.user.UserUpdateRequest;
import com.distcomp.errorhandling.exceptions.BusinessValidationException;
import com.distcomp.errorhandling.exceptions.UserNotFoundException;
import com.distcomp.model.user.User;
import com.distcomp.validator.abstraction.BaseValidator;
import com.distcomp.validator.model.ValidationArgs;
import com.distcomp.validator.model.ValidationResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class UserValidator extends BaseValidator<UserCreateRequest, UserUpdateRequest> {

    private final UserReactiveRepository userRepository;

    public Mono<Void> validateUserExists(final Long id) {
        return checkNotNull(id, "id", "ID must not be null")
                .flatMap(r -> {
                    if (!r.isValid()) {
                        return Mono.error(new BusinessValidationException(r.errors()));
                    }
                    return checkEntityExists(userRepository, id, "id", "User not found with id: " + id);
                })
                .flatMap(r -> {
                    if (r.isValid()) {
                        return Mono.empty();
                    } else {
                        return Mono.error(new UserNotFoundException(r.errors()));
                    }
                });
    }

    public Mono<ValidationResult> checkUserExists(final Long userId) {
        if (userId == null) {
            return Mono.just(ValidationResult.of("userId", "User ID must not be null"));
        }
        return checkEntityExists(userRepository, userId, "userId", "User not found with id: " + userId);
    }

    private Mono<ValidationResult> checkLoginUnique(final String login, final Long excludeUserId) {
        if (login == null || login.isBlank()) {
            return Mono.just(ValidationResult.of("login", "Login must not be empty"));
        }
        
        return userRepository.findByLogin(login)
                .flatMap(existingUser -> checkForLoginExisting(excludeUserId, existingUser))
                .switchIfEmpty(Mono.just(ValidationResult.ok())); 
    }

    private static Mono<ValidationResult> checkForLoginExisting(final Long excludeUserId, final User existingUser) {
        return existingUser.getId().equals(excludeUserId) ?
                Mono.just(ValidationResult.ok()) :
                Mono.just(ValidationResult.of("login", "Login already exists"));
    }

    @Override
    public Mono<ValidationResult> validateUpdate(final UserUpdateRequest request, final ValidationArgs args) {
        final Long id = args.id();
        final String login = request.getLogin();

        Mono<ValidationResult> result = Mono.just(ValidationResult.ok());

        
        result = result.flatMap(r -> checkNotNull(id, "id", "ID must not be null")
                .map(r::merge));

        
        result = result.flatMap(r -> checkUserExists(r, id));


        result = checkForLoginChanges(login, result, id);


        return result.flatMap(r -> {
            if (r.isValid()) {
                return Mono.just(r);
            } else {
                return Mono.error(new BusinessValidationException(r.errors()));
            }
        });
    }

    private Mono<ValidationResult> checkForLoginChanges(final String login, Mono<ValidationResult> result, final Long id) {
        if (login != null && !login.isBlank()) {
            result = result.flatMap(r -> checkLoginUnique(login, id)
                    .map(r::merge));
        }
        return result;
    }

    private Mono<ValidationResult> checkUserExists(final ValidationResult r, final Long id) {
        if (id == null) {
            return Mono.just(r);
        }
        return checkEntityExists(userRepository, id, "id", "User not found with id: " + id)
                .map(r::merge);
    }

    @Override
    public Mono<ValidationResult> validateCreate(final UserCreateRequest request, final ValidationArgs args) {
        final String login = request.getLogin();

        Mono<ValidationResult> result = Mono.just(ValidationResult.ok());

        
        result = result.flatMap(r -> {
            if (login == null || login.isBlank()) {
                return Mono.just(r.merge(ValidationResult.of("login", "Login must not be empty")));
            }
            return Mono.just(r);
        });


        result = checkForLoginChanges(login, result, null);


        return result.flatMap(r -> {
            if (r.isValid()) {
                return Mono.just(r);
            } else {
                return Mono.error(new BusinessValidationException(r.errors()));
            }
        });
    }
}