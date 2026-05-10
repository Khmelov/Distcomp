package by.distcomp.app.controller;

import by.distcomp.app.dto.LoginRequestTo;
import by.distcomp.app.dto.UserRequestTo;
import by.distcomp.app.dto.UserResponseTo;
import by.distcomp.app.exception.ResourceNotFoundException;
import by.distcomp.app.security.JwtTokenProvider;
import by.distcomp.app.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import jakarta.validation.Valid;
import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/api/v2.0")
public class AuthController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;

    public AuthController(UserService userService,
                          PasswordEncoder passwordEncoder,
                          JwtTokenProvider tokenProvider) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
    }

    @PostMapping("/users")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponseTo register(@Valid @RequestBody UserRequestTo request) {
        String role = request.role();
        if (role == null || role.isBlank()) {
            role = "CUSTOMER";
        }
        UserRequestTo secureRequest = new UserRequestTo(
                request.login(),
                passwordEncoder.encode(request.password()),
                request.firstname(),
                request.lastname(),
                role
        );
        return userService.createUser(secureRequest);
    }

    @PostMapping("/login")
    public Map<String, String> login(@RequestBody LoginRequestTo loginRequest) {
        try {
            UserResponseTo user = userService.getUserByLogin(loginRequest.login());
            if (passwordEncoder.matches(loginRequest.password(), user.password())) {
                String token = tokenProvider.createToken(user.login(), user.role());
                return Collections.singletonMap("access_token", token);
            }
        } catch (ResourceNotFoundException e) {
        }
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid login or password");
    }
}