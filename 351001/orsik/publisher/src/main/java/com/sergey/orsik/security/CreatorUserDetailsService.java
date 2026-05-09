package com.sergey.orsik.security;

import com.sergey.orsik.entity.Creator;
import com.sergey.orsik.repository.CreatorRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CreatorUserDetailsService implements UserDetailsService {

    private final CreatorRepository creatorRepository;

    public CreatorUserDetailsService(CreatorRepository creatorRepository) {
        this.creatorRepository = creatorRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Creator creator = creatorRepository.findByLogin(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return User.builder()
                .username(creator.getLogin())
                .password(creator.getPassword())
                .roles(creator.getRole().name())
                .build();
    }
}
