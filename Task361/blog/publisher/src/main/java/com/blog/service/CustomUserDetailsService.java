package com.blog.service;

import com.blog.model.Editor;
import com.blog.repository.EditorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private EditorRepository editorRepository;

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        Editor editor = editorRepository.findByLogin(login)
                .orElseThrow(() -> new UsernameNotFoundException("Editor not found with login: " + login));

        return new CustomUserDetails(editor);
    }
}