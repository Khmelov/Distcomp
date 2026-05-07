package com.github.Lexya06.startrestapp.controller.realization;

import com.github.Lexya06.startrestapp.controller.abstraction.BaseController;
import com.github.Lexya06.startrestapp.model.dto.request.UserRequestTo;
import com.github.Lexya06.startrestapp.model.dto.response.UserResponseTo;
import com.github.Lexya06.startrestapp.model.entity.realization.User;
import com.github.Lexya06.startrestapp.service.abstraction.BaseEntityService;
import com.github.Lexya06.startrestapp.service.realization.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

}
