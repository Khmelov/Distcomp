package com.distcomp.controller.user;

import com.distcomp.dto.user.UserCreateRequest;
import com.distcomp.dto.user.UserPatchRequest;
import com.distcomp.dto.user.UserResponseDto;
import com.distcomp.dto.user.UserUpdateRequest;
import com.distcomp.service.security.AuthorizationService;
import com.distcomp.service.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v2.0/users")
@RequiredArgsConstructor
public class UserControllerV2 {

    private final UserService userService;
    private final AuthorizationService authorizationService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public Flux<UserResponseDto> getAll(
            @RequestParam(defaultValue = "0") final int page,
            @RequestParam(defaultValue = "10") final int size) {
        return userService.findAll(page, size);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public Mono<UserResponseDto> getById(@PathVariable final Long id) {
        return userService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<UserResponseDto> create(@Valid @RequestBody final UserCreateRequest request) {
        return userService.create(request);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<UserResponseDto> update(@PathVariable final Long id,
                                        @Valid @RequestBody final UserUpdateRequest request) {
        return ReactiveSecurityContextHolder.getContext()
                .mapNotNull(SecurityContext::getAuthentication)
                .flatMap(auth -> {
                    final boolean isAdmin = auth.getAuthorities().stream()
                            .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
                    final String login = auth.getName();

                    return authorizationService.isUserSelf(id, login)
                            .flatMap(isSelf -> {
                                if (!isAdmin && !isSelf) {
                                    return Mono.error(new AccessDeniedException("Access denied"));
                                }
                                return userService.update(id, request);
                            });
                });
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<UserResponseDto> patch(@PathVariable final Long id,
                                       @RequestBody final UserPatchRequest request) {
        return ReactiveSecurityContextHolder.getContext()
                .mapNotNull(SecurityContext::getAuthentication)
                .flatMap(auth -> {
                    final boolean isAdmin = auth.getAuthorities().stream()
                            .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
                    final String login = auth.getName();

                    return authorizationService.isUserSelf(id, login)
                            .flatMap(isSelf -> {
                                if (!isAdmin && !isSelf) {
                                    return Mono.error(new AccessDeniedException("Access denied"));
                                }
                                return userService.patch(id, request);
                            });
                });
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> delete(@PathVariable final Long id) {
        return ReactiveSecurityContextHolder.getContext()
                .mapNotNull(SecurityContext::getAuthentication)
                .flatMap(auth -> {
                    final boolean isAdmin = auth.getAuthorities().stream()
                            .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
                    final String login = auth.getName();

                    return authorizationService.isUserSelf(id, login)
                            .flatMap(isSelf -> {
                                if (!isAdmin && !isSelf) {
                                    return Mono.error(new AccessDeniedException("Access denied"));
                                }
                                return userService.delete(id);
                            });
                });
    }
}