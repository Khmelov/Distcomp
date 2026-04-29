package by.distcomp.app.security;

import by.distcomp.app.dto.UserResponseTo;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.security.core.userdetails.UserDetailsService;
@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final RestClient restClient;

    public CustomUserDetailsService(RestClient restClient) {
        this.restClient = restClient;
    }

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        UserResponseTo user = restClient.get()
                .uri("/users/login/{login}", login)
                .retrieve()
                .body(UserResponseTo.class);

        if (user == null) throw new UsernameNotFoundException("User not found");

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.login())
                .password(user.password())
                .authorities("ROLE_" + user.role())
                .build();
    }
}