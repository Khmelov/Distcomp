package org.rv.lab1.security;

import org.rv.lab1.domain.EditorRole;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

public record EditorPrincipal(String login, long editorId, EditorRole role) implements Serializable {

    public Collection<? extends GrantedAuthority> authorities() {
        if (role == EditorRole.ADMIN) {
            return List.of(new SimpleGrantedAuthority("ROLE_ADMIN"), new SimpleGrantedAuthority("ROLE_CUSTOMER"));
        }
        return List.of(new SimpleGrantedAuthority("ROLE_CUSTOMER"));
    }
}
