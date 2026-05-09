package com.github.Lexya06.startrestapp.publisher.impl.controller.realization;

import com.github.Lexya06.startrestapp.publisher.impl.controller.abstraction.BaseController;
import com.github.Lexya06.startrestapp.publisher.api.dto.user.UserRequestTo;
import com.github.Lexya06.startrestapp.publisher.api.dto.user.UserResponseTo;
import com.github.Lexya06.startrestapp.publisher.impl.model.entity.realization.User;
import com.github.Lexya06.startrestapp.publisher.impl.service.abstraction.BaseEntityService;
import com.github.Lexya06.startrestapp.publisher.impl.service.realization.UserService;
import com.querydsl.core.types.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("${server.api.base-path.v1}/users")
@Validated
public class UserController extends BaseController<User, UserRequestTo, UserResponseTo> {
    UserService userService;
    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }
    @Override
    protected BaseEntityService<User, UserRequestTo, UserResponseTo> getBaseService() {
        return userService;
    }

    @Override
    public ResponseEntity<List<UserResponseTo>> getAllEntities(@QuerydslPredicate(root = User.class) Predicate predicate, Pageable pageable) {
        return getAllEntitiesBase(predicate, pageable);
    }

}
