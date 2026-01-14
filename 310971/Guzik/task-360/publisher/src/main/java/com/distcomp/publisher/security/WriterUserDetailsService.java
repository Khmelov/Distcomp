package com.distcomp.publisher.security;

import com.distcomp.publisher.writer.domain.Writer;
import com.distcomp.publisher.writer.repo.WriterRepository;
import java.util.List;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class WriterUserDetailsService implements UserDetailsService {

    private final WriterRepository writerRepository;

    public WriterUserDetailsService(WriterRepository writerRepository) {
        this.writerRepository = writerRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Writer writer = writerRepository.findByLogin(username)
                .orElseThrow(() -> new UsernameNotFoundException("Writer not found"));

        String role = writer.getRole() != null ? writer.getRole().name() : "CUSTOMER";

        return new User(
                writer.getLogin(),
                writer.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_" + role))
        );
    }
}
