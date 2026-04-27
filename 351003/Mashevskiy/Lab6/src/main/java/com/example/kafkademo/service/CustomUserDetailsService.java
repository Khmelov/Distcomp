package com.example.kafkademo.service;

import com.example.kafkademo.entity.Creator;
import com.example.kafkademo.repository.CreatorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private CreatorRepository creatorRepository;

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        Creator creator = creatorRepository.findByLogin(login)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + login));

        return new User(
                creator.getLogin(),
                creator.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + creator.getRole().name()))
        );
    }
}