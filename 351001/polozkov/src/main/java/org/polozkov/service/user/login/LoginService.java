package org.polozkov.service.user.login;

import lombok.RequiredArgsConstructor;
import org.polozkov.dto.login.LoginRequestTo;
import org.polozkov.dto.login.LoginResponseTo;
import org.polozkov.entity.user.User;
import org.polozkov.exception.NotFoundException;
import org.polozkov.exception.UnauthorizedException;
import org.polozkov.repository.user.UserRepository;
import org.polozkov.security.jwt.JwtTokenProvider;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoginService {

    private final UserRepository userRepository;
    private final JwtTokenProvider tokenProvider;

    public LoginResponseTo login (LoginRequestTo dto) {
        User user = userRepository.findByLoginAndPassword(dto.getLogin(), dto.getPassword()).orElseThrow(()->
                new UnauthorizedException("user-not-found"));
        return new LoginResponseTo(tokenProvider.generateAccessToken(user.getLogin(), user.getRole().toString()));
    }

}
