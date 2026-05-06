package by.bsuir.distcomp.security;

import by.bsuir.distcomp.model.EditorRole;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class EditorAuthPrincipal implements UserDetails {

    private final Long editorId;
    private final String login;
    private final EditorRole role;
    private final String passwordHash;

    public EditorAuthPrincipal(Long editorId, String login, EditorRole role, String passwordHash) {
        this.editorId = editorId;
        this.login = login;
        this.role = role;
        this.passwordHash = passwordHash;
    }

    public Long getEditorId() {
        return editorId;
    }

    public String getLogin() {
        return login;
    }

    public EditorRole getRole() {
        return role;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public String getPassword() {
        return passwordHash;
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
