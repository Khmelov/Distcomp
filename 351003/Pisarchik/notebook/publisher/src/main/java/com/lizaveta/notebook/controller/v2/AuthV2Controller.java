package com.lizaveta.notebook.controller.v2;

import com.lizaveta.notebook.model.dto.request.LoginRequestTo;
import com.lizaveta.notebook.model.dto.response.AccessTokenResponseTo;
import com.lizaveta.notebook.service.AuthenticationV2Service;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v2.0")
public class AuthV2Controller {

    private final AuthenticationV2Service authenticationV2Service;

    public AuthV2Controller(final AuthenticationV2Service authenticationV2Service) {
        this.authenticationV2Service = authenticationV2Service;
    }

    @PostMapping("/login")
    public AccessTokenResponseTo login(@Valid @RequestBody final LoginRequestTo request) {
        return authenticationV2Service.login(request);
    }
}
