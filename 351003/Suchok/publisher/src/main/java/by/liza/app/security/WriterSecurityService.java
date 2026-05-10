package by.liza.app.security;

import by.liza.app.repository.WriterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service("writerSecurity")
@RequiredArgsConstructor
public class WriterSecurityService {

    private final WriterRepository writerRepository;

    public boolean isOwner(Authentication auth, Long writerId) {
        return writerRepository.findById(writerId)
                .map(w -> w.getLogin().equals(auth.getName()))
                .orElse(false);
    }
}
