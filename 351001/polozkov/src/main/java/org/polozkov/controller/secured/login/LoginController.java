package org.polozkov.controller.secured.login;

import lombok.RequiredArgsConstructor;
import org.polozkov.dto.login.LoginRequestTo;
import org.polozkov.dto.login.LoginResponseTo;
import org.polozkov.service.user.login.LoginService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v2.0/login")
@RequiredArgsConstructor
public class LoginController {

    private final LoginService loginService;

    @PostMapping()
    public LoginResponseTo login(@RequestBody LoginRequestTo dto) {
        return loginService.login(dto);
    }

}
