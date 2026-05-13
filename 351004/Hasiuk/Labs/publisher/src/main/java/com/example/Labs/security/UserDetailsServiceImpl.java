package com.example.Labs.security;
import com.example.Labs.entity.Editor;
import com.example.Labs.repository.EditorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.List;
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    private final EditorRepository repository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Editor editor = repository.findByLogin(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        return new User(editor.getLogin(), editor.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_" + editor.getRole())));
    }
}