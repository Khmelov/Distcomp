package com.github.Lexya06.startrestapp.publisher.impl.controller.realization.security;

import com.github.Lexya06.startrestapp.publisher.impl.config.security.JwtUtils;
import com.github.Lexya06.startrestapp.publisher.impl.controller.realization.security.dto.LoginRequest;
import com.github.Lexya06.startrestapp.publisher.impl.controller.realization.security.dto.LoginResponse;
import com.github.Lexya06.startrestapp.publisher.impl.model.entity.realization.User;
import com.github.Lexya06.startrestapp.publisher.impl.model.repository.realization.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v2.0")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtUtils jwtUtils;
    private final UserRepository userRepository;

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getLogin(), request.getPassword())
        );
        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getLogin());
        User user = userRepository.findByLogin(request.getLogin())
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + request.getLogin()));
        String jwt = jwtUtils.generateToken(userDetails, user.getRole().name());
        return LoginResponse.builder().accessToken(jwt).build();
    }
}
