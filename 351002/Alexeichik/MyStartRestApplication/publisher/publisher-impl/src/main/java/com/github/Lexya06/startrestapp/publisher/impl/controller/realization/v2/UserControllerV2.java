package com.github.Lexya06.startrestapp.publisher.impl.controller.realization.v2;

import com.github.Lexya06.startrestapp.publisher.api.dto.user.UserRequestTo;
import com.github.Lexya06.startrestapp.publisher.api.dto.user.UserResponseTo;
import com.github.Lexya06.startrestapp.publisher.impl.controller.abstraction.BaseController;
import com.github.Lexya06.startrestapp.publisher.impl.model.entity.realization.User;
import com.github.Lexya06.startrestapp.publisher.impl.service.abstraction.BaseEntityService;
import com.github.Lexya06.startrestapp.publisher.impl.service.realization.UserService;
import com.querydsl.core.types.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v2.0/users")
@Validated
public class UserControllerV2 extends BaseController<User, UserRequestTo, UserResponseTo> {
    private final UserService userService;

    @Autowired
    public UserControllerV2(UserService userService) {
        this.userService = userService;
    }

    @Override
    protected BaseEntityService<User, UserRequestTo, UserResponseTo> getBaseService() {
        return userService;
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponseTo>> getAllEntities(@QuerydslPredicate(root = User.class) Predicate predicate, Pageable pageable) {
        return getAllEntitiesBase(predicate, pageable);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN') or (hasRole('CUSTOMER') and #id == principal.id)")
    public ResponseEntity<UserResponseTo> get(Long id) {
        return super.get(id);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN') or (hasRole('CUSTOMER') and #id == principal.id)")
    public ResponseEntity<UserResponseTo> updateEntity(Long id, UserRequestTo requestDTO) {
        return super.updateEntity(id, requestDTO);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteEntity(Long id) {
        return super.deleteEntity(id);
    }
}
