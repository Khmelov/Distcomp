package by.bsuir.task361.publisher.security;

import by.bsuir.task361.publisher.config.SecurityConfig;
import by.bsuir.task361.publisher.controller.AuthController;
import by.bsuir.task361.publisher.controller.ReactionV2Controller;
import by.bsuir.task361.publisher.controller.UserController;
import by.bsuir.task361.publisher.controller.UserV2Controller;
import by.bsuir.task361.publisher.dto.ReactionState;
import by.bsuir.task361.publisher.dto.response.LoginResponseTo;
import by.bsuir.task361.publisher.dto.response.ReactionResponseTo;
import by.bsuir.task361.publisher.dto.response.UserResponseTo;
import by.bsuir.task361.publisher.entity.User;
import by.bsuir.task361.publisher.entity.UserRole;
import by.bsuir.task361.publisher.service.AuthenticationService;
import by.bsuir.task361.publisher.service.SecuredReactionService;
import by.bsuir.task361.publisher.service.SecuredUserService;
import by.bsuir.task361.publisher.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {
        UserController.class,
        UserV2Controller.class,
        ReactionV2Controller.class,
        AuthController.class
})
@Import({
        SecurityConfig.class,
        JwtAuthenticationFilter.class,
        JwtAuthenticationEntryPoint.class,
        JwtAccessDeniedHandler.class,
        JwtTokenService.class
})
@TestPropertySource(properties = {
        "app.jwt.secret=task361-jwt-secret-task361-jwt-secret-1234567890",
        "app.jwt.expiration-seconds=3600"
})
class SecurityWebMvcTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtTokenService jwtTokenService;

    @MockBean
    private UserService userService;
    @MockBean
    private SecuredUserService securedUserService;
    @MockBean
    private SecuredReactionService securedReactionService;
    @MockBean
    private AuthenticationService authenticationService;
    @MockBean
    private PublisherUserDetailsService publisherUserDetailsService;

    private String customerToken;

    @BeforeEach
    void setUp() {
        User user = new User(1L, "customer", "$2a$10$abcdefghijklmnopqrstuv", UserRole.CUSTOMER, "Ivan", "Ivanov");
        customerToken = jwtTokenService.generateLoginResponse(user).accessToken();
    }

    @Test
    void v1EndpointIsAccessibleWithoutToken() throws Exception {
        when(userService.findById(1L)).thenReturn(new UserResponseTo(1L, "public-user", "Ivan", "Ivanov", UserRole.CUSTOMER));

        mockMvc.perform(get("/api/v1.0/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.login").value("public-user"));
    }

    @Test
    void registrationEndpointIsAccessibleWithoutToken() throws Exception {
        when(securedUserService.register(any())).thenReturn(new UserResponseTo(2L, "registered", "Petr", "Petrov", UserRole.CUSTOMER));

        mockMvc.perform(post("/api/v2.0/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "login": "registered",
                                  "password": "password123",
                                  "firstName": "Petr",
                                  "lastName": "Petrov",
                                  "role": "CUSTOMER"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.login").value("registered"))
                .andExpect(jsonPath("$.role").value("CUSTOMER"));
    }

    @Test
    void loginEndpointReturnsAccessToken() throws Exception {
        when(authenticationService.login(any())).thenReturn(new LoginResponseTo("jwt-token", "Bearer", UserRole.CUSTOMER, 3600));

        mockMvc.perform(post("/api/v2.0/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "login": "customer",
                                  "password": "password123"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.access_token").value("jwt-token"))
                .andExpect(jsonPath("$.token_type").value("Bearer"));
    }

    @Test
    void protectedReactionEndpointWithoutTokenReturns401() throws Exception {
        mockMvc.perform(get("/api/v2.0/reactions"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.errorCode").value(40101))
                .andExpect(jsonPath("$.errorMessage").value("Authentication required"));
    }

    @Test
    void protectedReactionEndpointWithValidTokenReturnsSuccess() throws Exception {
        UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                "customer",
                "encoded",
                List.of(new SimpleGrantedAuthority("ROLE_CUSTOMER"))
        );
        when(publisherUserDetailsService.loadUserByUsername("customer")).thenReturn(userDetails);
        when(securedReactionService.findAll()).thenReturn(List.of(
                new ReactionResponseTo(5L, 11L, "cached reaction", ReactionState.APPROVE)
        ));

        mockMvc.perform(get("/api/v2.0/reactions")
                        .header("Authorization", "Bearer " + customerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(5L));
    }
}
