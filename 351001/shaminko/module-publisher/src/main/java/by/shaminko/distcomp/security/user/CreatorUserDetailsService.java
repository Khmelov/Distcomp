package by.shaminko.distcomp.security.user;

import by.shaminko.distcomp.repositories.EditorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CreatorUserDetailsService implements UserDetailsService {
    private final EditorRepository editorRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var editor = editorRepository.findByLogin(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return new AuthenticatedUser(editor.getId(), editor.getLogin(), editor.getPassword(), editor.getRole());
    }
}

