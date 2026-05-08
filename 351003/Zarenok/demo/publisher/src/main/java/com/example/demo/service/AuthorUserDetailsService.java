package com.example.demo.service;

import com.example.demo.model.Author;
import com.example.demo.repository.AuthorRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AuthorUserDetailsService implements UserDetailsService {

    private final AuthorRepository authorRepository;

    public AuthorUserDetailsService(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        Author author = authorRepository.findByLogin(login)
                .orElseThrow(() -> new UsernameNotFoundException("Invalid credentials"));
        return User.builder()
                .username(author.getLogin())
                .password(author.getPassword())
                .authorities("ROLE_" + author.getRole())
                .build();
    }
}
