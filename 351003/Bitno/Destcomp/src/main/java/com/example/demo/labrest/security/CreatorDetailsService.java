package com.example.demo.labrest.security;

import com.example.demo.labrest.model.Creator;
import com.example.demo.labrest.repository.CreatorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CreatorDetailsService implements UserDetailsService {

    private final CreatorRepository creatorRepo;

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        Creator creator = creatorRepo.findByLogin(login)
                .orElseThrow(() -> new UsernameNotFoundException("Creator not found: " + login));

        return User.builder()
                .username(creator.getLogin())
                .password(creator.getPassword())
                .roles(creator.getRole())
                .build();
    }
}