package org.rv.lab1.controller.v2;

import jakarta.validation.Valid;
import org.rv.lab1.api.ApiPaths;
import org.rv.lab1.dto.LoginRequestTo;
import org.rv.lab1.dto.LoginResponseTo;
import org.rv.lab1.service.EditorService;
import org.rv.lab1.security.JwtService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiPaths.API_V2)
public class AuthV2Controller {

    private final EditorService editorService;
    private final JwtService jwtService;

    public AuthV2Controller(EditorService editorService, JwtService jwtService) {
        this.editorService = editorService;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public LoginResponseTo login(@Valid @RequestBody LoginRequestTo request) {
        var editor = editorService.authenticate(request.login(), request.password());
        return new LoginResponseTo(jwtService.createAccessToken(editor));
    }
}
