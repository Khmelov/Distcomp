package com.adashkevich.rest.lab.controller;

import com.adashkevich.rest.lab.dto.auth.LoginRequestTo;
import com.adashkevich.rest.lab.dto.auth.LoginResponseTo;
import com.adashkevich.rest.lab.dto.response.EditorResponseTo;
import com.adashkevich.rest.lab.exception.UnauthorizedException;
import com.adashkevich.rest.lab.model.Editor;
import com.adashkevich.rest.lab.security.JwtService;
import com.adashkevich.rest.lab.service.EditorService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v2.0")
public class AuthController {
    private final EditorService editorService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    public AuthController(EditorService editorService, JwtService jwtService, PasswordEncoder passwordEncoder) {
        this.editorService = editorService;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public LoginResponseTo login(@Valid @RequestBody LoginRequestTo body) {
        Editor editor = editorService.findByLogin(body.login)
                .orElseThrow(() -> new UnauthorizedException("Invalid login or password", "40101"));

        boolean encryptedPasswordMatches = editor.getPassword() != null && editor.getPassword().startsWith("$2")
                && passwordEncoder.matches(body.password, editor.getPassword());
        boolean plainPasswordMatches = body.password.equals(editor.getPassword());
        if (!encryptedPasswordMatches && !plainPasswordMatches) {
            throw new UnauthorizedException("Invalid login or password", "40101");
        }

        return new LoginResponseTo(jwtService.generateToken(editor.getLogin(), editor.getRole()));
    }

    @GetMapping("/editors/me")
    public EditorResponseTo currentUser(Authentication authentication) {
        Editor editor = editorService.findByLogin(authentication.getName())
                .orElseThrow(() -> new UnauthorizedException("Current user not found", "40102"));
        return editorService.getById(editor.getId());
    }

    @GetMapping("/editors/me/role")
    public Map<String, String> currentUserRole(Authentication authentication) {
        Editor editor = editorService.findByLogin(authentication.getName())
                .orElseThrow(() -> new UnauthorizedException("Current user not found", "40102"));
        return Map.of("role", editor.getRole().name());
    }
}
