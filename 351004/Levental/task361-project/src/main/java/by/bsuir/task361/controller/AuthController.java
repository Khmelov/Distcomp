
package by.bsuir.task361.controller;

import by.bsuir.task361.entity.Creator;
import by.bsuir.task361.repo.CreatorRepository;
import by.bsuir.task361.security.JwtService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v2.0")
public class AuthController {

    private final CreatorRepository repo;
    private final JwtService jwt;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public AuthController(CreatorRepository repo, JwtService jwt) {
        this.repo = repo;
        this.jwt = jwt;
    }

    @PostMapping("/creators")
    public Creator register(@RequestBody Creator c) {
        c.setPassword(encoder.encode(c.getPassword()));
        return repo.save(c);
    }

    @PostMapping("/login")
    public Map<String,String> login(@RequestBody Creator c) {
        Creator user = repo.findByLogin(c.getLogin()).orElseThrow();
        if (!encoder.matches(c.getPassword(), user.getPassword()))
            throw new RuntimeException("Bad credentials");
        String token = jwt.generate(user.getLogin(), user.getRole());
        return Map.of("access_token", token);
    }
}
