package com.lizaveta.notebook.security;

import com.lizaveta.notebook.model.UserRole;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public final class SecurityWriter implements UserDetails {

    private final long writerId;
    private final String login;
    private final UserRole userRole;

    public SecurityWriter(final long writerId, final String login, final UserRole userRole) {
        this.writerId = writerId;
        this.login = login;
        this.userRole = userRole;
    }

    public long getWriterId() {
        return writerId;
    }

    public UserRole getUserRole() {
        return userRole;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + userRole.name()));
    }

    @Override
    public String getPassword() {
        return "";
    }

    @Override
    public String getUsername() {
        return login;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
