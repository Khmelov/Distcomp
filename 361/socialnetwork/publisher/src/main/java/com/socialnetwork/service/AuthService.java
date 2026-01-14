package com.socialnetwork.service;

import com.socialnetwork.dto.request.LoginRequest;
import com.socialnetwork.dto.request.RegisterRequest;
import com.socialnetwork.dto.response.LoginResponse;
import com.socialnetwork.dto.response.UserResponseTo;

public interface AuthService {
    UserResponseTo register(RegisterRequest request);
    LoginResponse login(LoginRequest request);
}

