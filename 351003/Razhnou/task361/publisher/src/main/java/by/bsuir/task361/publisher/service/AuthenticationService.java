package by.bsuir.task361.publisher.service;

import by.bsuir.task361.publisher.dto.request.LoginRequestTo;
import by.bsuir.task361.publisher.dto.response.LoginResponseTo;
import by.bsuir.task361.publisher.entity.User;
import by.bsuir.task361.publisher.exception.ApiException;
import by.bsuir.task361.publisher.exception.BadRequestException;
import by.bsuir.task361.publisher.exception.NotFoundException;
import by.bsuir.task361.publisher.security.JwtTokenService;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenService jwtTokenService;

    public AuthenticationService(
            UserService userService,
            PasswordEncoder passwordEncoder,
            JwtTokenService jwtTokenService
    ) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenService = jwtTokenService;
    }

    public LoginResponseTo login(LoginRequestTo request) {
        validateLogin(request.login());
        validatePassword(request.password());
        User user;
        try {
            user = userService.getUserByLogin(request.login());
        } catch (NotFoundException exception) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, 40101, "Invalid login or password");
        }
        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, 40101, "Invalid login or password");
        }
        return jwtTokenService.generateLoginResponse(user);
    }

    private void validateLogin(String login) {
        if (login == null || login.trim().isEmpty()) {
            throw new BadRequestException("User login must not be blank", 2);
        }
    }

    private void validatePassword(String password) {
        if (password == null || password.trim().isEmpty()) {
            throw new BadRequestException("User password must not be blank", 4);
        }
    }
}
