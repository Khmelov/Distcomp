package com.example.Labs.controller;
import com.example.Labs.dto.request.LoginRequest;
import com.example.Labs.dto.response.JwtResponse;
import com.example.Labs.entity.Editor;
import com.example.Labs.repository.EditorRepository;
import com.example.Labs.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/v2.0/login")
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationManager authManager;
    private final JwtUtils jwtUtils;
    private final EditorRepository editorRepository;
    @PostMapping
    public JwtResponse login(@RequestBody LoginRequest request) {
        authManager.authenticate(new UsernamePasswordAuthenticationToken(request.getLogin(), request.getPassword()));
        Editor editor = editorRepository.findByLogin(request.getLogin()).orElseThrow();
        return new JwtResponse(jwtUtils.generateToken(editor.getLogin(), editor.getRole()));
    }
}