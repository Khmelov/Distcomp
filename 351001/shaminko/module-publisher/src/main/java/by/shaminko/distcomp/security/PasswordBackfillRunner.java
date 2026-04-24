package by.shaminko.distcomp.security;

import by.shaminko.distcomp.repositories.EditorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PasswordBackfillRunner implements CommandLineRunner {
    private final EditorRepository editorRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        // Ensures we don't keep plaintext passwords from old DB state or seed data.
        editorRepository.findAll().forEach(editor -> {
            String password = editor.getPassword();
            if (password != null && !password.isBlank() && !password.startsWith("$2")) {
                editor.setPassword(passwordEncoder.encode(password));
                editorRepository.save(editor);
            }
        });
    }
}

