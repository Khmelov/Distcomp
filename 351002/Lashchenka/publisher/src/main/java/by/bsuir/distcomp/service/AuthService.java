package by.bsuir.distcomp.service;

import by.bsuir.distcomp.dto.request.EditorRegistrationTo;
import by.bsuir.distcomp.dto.request.LoginRequestTo;
import by.bsuir.distcomp.dto.response.EditorResponseTo;
import by.bsuir.distcomp.dto.response.LoginResponseTo;
import by.bsuir.distcomp.entity.Editor;
import by.bsuir.distcomp.exception.DuplicateException;
import by.bsuir.distcomp.model.EditorRole;
import by.bsuir.distcomp.exception.ResourceNotFoundException;
import by.bsuir.distcomp.exception.UnauthorizedException;
import by.bsuir.distcomp.mapper.EditorMapper;
import by.bsuir.distcomp.repository.EditorRepository;
import by.bsuir.distcomp.security.EditorAuthPrincipal;
import by.bsuir.distcomp.security.JwtService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AuthService {

    private final EditorRepository editorRepository;
    private final EditorMapper editorMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(
            EditorRepository editorRepository,
            EditorMapper editorMapper,
            PasswordEncoder passwordEncoder,
            JwtService jwtService) {
        this.editorRepository = editorRepository;
        this.editorMapper = editorMapper;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public LoginResponseTo login(LoginRequestTo req) {
        Editor editor = editorRepository.findByLogin(req.getLogin())
                .orElseThrow(() -> new UnauthorizedException("Invalid login or password", 40100));
        if (!passwordEncoder.matches(req.getPassword(), editor.getPassword())) {
            throw new UnauthorizedException("Invalid login or password", 40100);
        }
        String token = jwtService.createToken(editor.getLogin(), editor.getId(), editor.getRole());
        return new LoginResponseTo(token);
    }

    @CacheEvict(value = "editors", key = "'all'")
    public EditorResponseTo register(EditorRegistrationTo dto) {
        if (editorRepository.existsByLogin(dto.getLogin())) {
            throw new DuplicateException("Editor with login '" + dto.getLogin() + "' already exists", 40301);
        }
        Editor e = new Editor();
        e.setLogin(dto.getLogin());
        e.setPassword(passwordEncoder.encode(dto.getPassword()));
        e.setFirstname(dto.getFirstName());
        e.setLastname(dto.getLastName());
        e.setRole(dto.getRole() != null ? dto.getRole() : EditorRole.CUSTOMER);
        Editor saved = editorRepository.save(e);
        return editorMapper.toResponseDto(saved);
    }

    @Transactional(readOnly = true)
    public EditorResponseTo currentEditor() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof EditorAuthPrincipal p)) {
            throw new UnauthorizedException("Authentication required", 40101);
        }
        Editor editor = editorRepository.findById(p.getEditorId())
                .orElseThrow(() -> new ResourceNotFoundException("Editor not found", 40401));
        return editorMapper.toResponseDto(editor);
    }
}
