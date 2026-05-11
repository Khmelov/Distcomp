package by.bsuir.publisher.security;

import by.bsuir.publisher.repositories.WriterRepository;
import by.bsuir.publisher.domain.Writer;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final WriterRepository writerRepository;

    @Override
    public UserDetails loadUserByUsername(String login) {
        Writer w = writerRepository.findByLogin(login)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + login));
        return User.withUsername(w.getLogin())
                .password(w.getPassword())
                .authorities("ROLE_" + w.getRole().name())
                .build();
    }
}
