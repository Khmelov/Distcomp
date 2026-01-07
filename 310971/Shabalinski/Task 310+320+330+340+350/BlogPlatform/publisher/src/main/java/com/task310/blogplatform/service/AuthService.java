package com.task310.blogplatform.service;

import com.task310.blogplatform.dto.UserRequestTo;
import com.task310.blogplatform.dto.UserResponseTo;
import com.task310.blogplatform.dto.auth.LoginRequest;
import com.task310.blogplatform.dto.auth.LoginResponse;
import com.task310.blogplatform.dto.auth.RegisterRequest;
import com.task310.blogplatform.exception.EntityNotFoundException;
import com.task310.blogplatform.exception.ValidationException;
import com.task310.blogplatform.model.Role;
import com.task310.blogplatform.model.User;
import com.task310.blogplatform.repository.UserRepository;
import com.task310.blogplatform.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Autowired
    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public UserResponseTo register(RegisterRequest request) {
        validateRegisterRequest(request);
        
        // Check if user already exists
        if (userRepository.findByLogin(request.getLogin().trim()).isPresent()) {
            throw new ValidationException("User with login '" + request.getLogin() + "' already exists");
        }
        
        User user = new User();
        user.setLogin(request.getLogin().trim());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFirstname(request.getFirstname().trim());
        user.setLastname(request.getLastname().trim());
        
        // Set role
        if (request.getRole() != null && !request.getRole().trim().isEmpty()) {
            try {
                user.setRole(Role.valueOf(request.getRole().trim().toUpperCase()));
            } catch (IllegalArgumentException e) {
                throw new ValidationException("Invalid role: " + request.getRole() + ". Valid roles are: ADMIN, CUSTOMER");
            }
        } else {
            user.setRole(Role.CUSTOMER);
        }
        
        User saved = userRepository.save(user);
        
        UserResponseTo response = new UserResponseTo();
        response.setId(saved.getId());
        response.setLogin(saved.getLogin());
        response.setPassword(saved.getPassword()); // Return encoded password
        response.setFirstname(saved.getFirstname());
        response.setLastname(saved.getLastname());
        response.setCreated(saved.getCreated());
        response.setModified(saved.getModified());
        
        return response;
    }

    public LoginResponse login(LoginRequest request) {
        validateLoginRequest(request);
        
        User user = userRepository.findByLogin(request.getLogin().trim())
                .orElseThrow(() -> new BadCredentialsException("Invalid login or password"));
        
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Invalid login or password");
        }
        
        String token = jwtUtil.generateToken(user.getLogin(), user.getRole());
        return new LoginResponse(token);
    }

    private void validateRegisterRequest(RegisterRequest request) {
        if (request == null) {
            throw new ValidationException("Registration data is required");
        }
        if (request.getLogin() == null || request.getLogin().trim().isEmpty()) {
            throw new ValidationException("Login is required");
        }
        if (request.getLogin().trim().length() < 2) {
            throw new ValidationException("Login must be at least 2 characters long");
        }
        if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            throw new ValidationException("Password is required");
        }
        if (request.getPassword().trim().length() < 8) {
            throw new ValidationException("Password must be at least 8 characters long");
        }
        if (request.getFirstname() == null || request.getFirstname().trim().isEmpty()) {
            throw new ValidationException("First name is required");
        }
        if (request.getFirstname().trim().length() < 2) {
            throw new ValidationException("First name must be at least 2 characters long");
        }
        if (request.getLastname() == null || request.getLastname().trim().isEmpty()) {
            throw new ValidationException("Last name is required");
        }
        if (request.getLastname().trim().length() < 2) {
            throw new ValidationException("Last name must be at least 2 characters long");
        }
    }

    private void validateLoginRequest(LoginRequest request) {
        if (request == null) {
            throw new ValidationException("Login data is required");
        }
        if (request.getLogin() == null || request.getLogin().trim().isEmpty()) {
            throw new ValidationException("Login is required");
        }
        if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            throw new ValidationException("Password is required");
        }
    }
}

