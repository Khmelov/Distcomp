package com.bsuir.distcomp.service;

import com.bsuir.distcomp.entity.Role;
import com.bsuir.distcomp.entity.Writer;
import com.bsuir.distcomp.repository.WriterRepository;
import com.bsuir.distcomp.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final WriterRepository writerRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;


    public String login(String login, String password) {
        Writer writer = writerRepository.findByLogin(login)
                .orElseThrow(() -> new BadCredentialsException("Invalid login or password"));

        if (!passwordEncoder.matches(password, writer.getPassword())) {
            throw new BadCredentialsException("Invalid login or password");
        }

        return jwtUtil.generateToken(writer.getLogin(), writer.getRole().name());
    }

}