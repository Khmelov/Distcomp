package by.boukhvalova.distcomp.security;

import by.shaminko.distcomp.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PasswordBackfillRunner implements CommandLineRunner {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        userRepository.findAll().forEach(user -> {
            String password = user.getPassword();
            if (password != null && !password.isBlank() && !password.startsWith("$2")) {
                user.setPassword(passwordEncoder.encode(password));
                userRepository.save(user);
            }
        });
    }
}
