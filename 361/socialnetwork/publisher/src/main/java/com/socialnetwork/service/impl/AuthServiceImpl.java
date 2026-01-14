package com.socialnetwork.service.impl;

import com.socialnetwork.dto.request.LoginRequest;
import com.socialnetwork.dto.request.RegisterRequest;
import com.socialnetwork.dto.response.LoginResponse;
import com.socialnetwork.dto.response.UserResponseTo;
import com.socialnetwork.exception.DuplicateResourceException;
import com.socialnetwork.mapper.UserMapper;
import com.socialnetwork.model.Role;
import com.socialnetwork.model.User;
import com.socialnetwork.repository.UserRepository;
import com.socialnetwork.security.CustomUserDetails;
import com.socialnetwork.security.JwtTokenProvider;
import com.socialnetwork.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Override
    public UserResponseTo register(RegisterRequest request) {
        if (userRepository.existsByLogin(request.getLogin())) {
            throw new DuplicateResourceException("User with login '" + request.getLogin() + "' already exists");
        }

        User user = new User();
        user.setLogin(request.getLogin());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFirstname(request.getFirstname());
        user.setLastname(request.getLastname());
        user.setRole(request.getRole() != null ? request.getRole() : Role.CUSTOMER);

        User savedUser = userRepository.save(user);
        return userMapper.toResponse(savedUser);
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getLogin(),
                        request.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        String token = tokenProvider.generateToken(userDetails.getUsername(), userDetails.getRole());

        return new LoginResponse(token);
    }
}

