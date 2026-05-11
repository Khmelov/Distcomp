package by.bsuir.publisher.security;

import by.bsuir.publisher.repositories.WriterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component("writerSecurity")
@RequiredArgsConstructor
public class WriterSecurity {

    private final WriterRepository writerRepository;

    public boolean isOwner(Long writerId, String login) {
        if (writerId == null || login == null) {
            return false;
        }
        return writerRepository.findById(writerId)
                .map(w -> login.equals(w.getLogin()))
                .orElse(false);
    }
}
