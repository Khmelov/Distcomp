package by.boukhvalova.distcomp.controllers.v2;

import by.boukhvalova.distcomp.dto.LoginRequestTo;
import by.boukhvalova.distcomp.dto.MeResponseTo;
import by.boukhvalova.distcomp.dto.TokenResponseTo;
import by.boukhvalova.distcomp.security.jwt.JwtService;
import by.boukhvalova.distcomp.security.user.AuthenticatedUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v2.0")
@RequiredArgsConstructor
public class AuthV2Controller {
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public TokenResponseTo login(@RequestBody @Valid LoginRequestTo request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getLogin(), request.getPassword())
        );

        AuthenticatedUser user = (AuthenticatedUser) authentication.getPrincipal();
        String token = jwtService.generateToken(user.getUsername(), user.getRole());
        return new TokenResponseTo(token, "Bearer");
    }

    @GetMapping("/auth/me")
    public MeResponseTo me(Authentication authentication) {
        AuthenticatedUser user = (AuthenticatedUser) authentication.getPrincipal();
        return new MeResponseTo(user.getId(), user.getUsername(), user.getRole());
    }
}

