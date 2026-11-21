package com.sakura.languageschool.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import com.sakura.languageschool.api.common.ApiUtil;
import com.sakura.languageschool.model.dto.AuthResponse;
import com.sakura.languageschool.service.AuthService;
import com.sakura.languageschool.service.StudentService;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private StudentService studentService;

    @Mock
    private AuthService authService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders
                .standaloneSetup(new AuthController(
                        studentService,
                        authService,
                        new ApiUtil()))
                .setValidator(validator)
                .build();
    }

    @Test
    void returnsBearerTokenResponseForSuccessfulLogin() throws Exception {
        when(authService.login(any()))
                .thenReturn(new AuthResponse("signed-token", "Bearer", 3600));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "student1",
                                  "password": "SakuraDemo123!"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("Login successful"))
                .andExpect(jsonPath("$.data.accessToken").value("signed-token"))
                .andExpect(jsonPath("$.data.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.data.expiresIn").value(3600));
    }

    @Test
    void returnsUnauthorizedForWrongPassword() throws Exception {
        when(authService.login(any()))
                .thenThrow(new BadCredentialsException("bad credentials"));

        assertUnauthorized();
    }

    @Test
    void returnsUnauthorizedForUnknownUser() throws Exception {
        when(authService.login(any()))
                .thenThrow(new UsernameNotFoundException("missing"));

        assertUnauthorized();
    }

    @Test
    void returnsUnauthorizedForDisabledUser() throws Exception {
        when(authService.login(any()))
                .thenThrow(new DisabledException("disabled"));

        assertUnauthorized();
    }

    @Test
    void rejectsInvalidLoginRequest() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "",
                                  "password": ""
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    private void assertUnauthorized() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "student1",
                                  "password": "wrong-password"
                                }
                                """))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code")
                        .value("AUTHENTICATION_FAILED"))
                .andExpect(jsonPath("$.message")
                        .value("Invalid username or password"));
    }
}
