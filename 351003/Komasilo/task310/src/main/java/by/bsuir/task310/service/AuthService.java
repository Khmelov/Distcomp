package by.bsuir.task310.service;

import by.bsuir.task310.dto.LoginRequestTo;
import by.bsuir.task310.dto.LoginResponseTo;
import by.bsuir.task310.model.Author;
import by.bsuir.task310.repository.AuthorRepository;
import by.bsuir.task310.security.JwtService;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AuthorRepository authorRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(AuthorRepository authorRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService) {
        this.authorRepository = authorRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public LoginResponseTo login(LoginRequestTo requestTo) {
        Author author = authorRepository.findByLogin(requestTo.getLogin())
                .orElseThrow(() -> new BadCredentialsException("Invalid credentials"));

        if (!passwordEncoder.matches(requestTo.getPassword(), author.getPassword())) {
            throw new BadCredentialsException("Invalid credentials");
        }

        String token = jwtService.generateToken(author);
        return new LoginResponseTo(token);
    }
}