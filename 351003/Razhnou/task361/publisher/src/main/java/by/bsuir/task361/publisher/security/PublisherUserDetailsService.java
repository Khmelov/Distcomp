package by.bsuir.task361.publisher.security;

import by.bsuir.task361.publisher.entity.User;
import by.bsuir.task361.publisher.service.UserService;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PublisherUserDetailsService implements UserDetailsService {
    private final UserService userService;

    public PublisherUserDetailsService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            User user = userService.getUserByLogin(username);
            return new org.springframework.security.core.userdetails.User(
                    user.getLogin(),
                    user.getPassword(),
                    List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
            );
        } catch (RuntimeException exception) {
            throw new UsernameNotFoundException("User not found", exception);
        }
    }
}
