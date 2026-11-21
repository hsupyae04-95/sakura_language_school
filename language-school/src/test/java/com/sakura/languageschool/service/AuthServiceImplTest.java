package com.sakura.languageschool.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Duration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;

import com.sakura.languageschool.bean.Mapper;
import com.sakura.languageschool.model.dto.AuthResponse;
import com.sakura.languageschool.model.dto.LoginRequest;
import com.sakura.languageschool.repository.RoleDao;
import com.sakura.languageschool.repository.UserDao;
import com.sakura.languageschool.repository.UserRoleDao;
import com.sakura.languageschool.security.JwtProperties;
import com.sakura.languageschool.security.JwtService;
import com.sakura.languageschool.security.SecurityUtil;
import com.sakura.languageschool.service.impl.AuthServiceImpl;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserDao userDao;

    @Mock
    private Mapper mapper;

    @Mock
    private RoleDao roleDao;

    @Mock
    private UserRoleDao userRoleDao;

    @Mock
    private SecurityUtil securityUtil;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @Mock
    private Authentication authentication;

    private AuthServiceImpl authService;

    @BeforeEach
    void setUp() {
        JwtProperties jwtProperties = new JwtProperties();
        jwtProperties.setAccessTokenTtl(Duration.ofHours(1));

        authService = new AuthServiceImpl(
                userDao,
                mapper,
                roleDao,
                userRoleDao,
                securityUtil,
                authenticationManager,
                jwtService,
                jwtProperties);
    }

    @Test
    void authenticatesCredentialsAndIssuesAccessToken() {
        LoginRequest request = new LoginRequest();
        request.setUsername("student1");
        request.setPassword("SakuraDemo123!");

        when(authenticationManager.authenticate(any()))
                .thenReturn(authentication);
        when(authentication.getName()).thenReturn("student1");
        when(jwtService.generateAccessToken("student1"))
                .thenReturn("signed-token");

        AuthResponse response = authService.login(request);

        assertThat(response.getAccessToken()).isEqualTo("signed-token");
        assertThat(response.getTokenType()).isEqualTo("Bearer");
        assertThat(response.getExpiresIn()).isEqualTo(3600L);
        verify(authenticationManager).authenticate(any());
        verify(jwtService).generateAccessToken("student1");
    }

    @Test
    void propagatesAuthenticationFailureWithoutIssuingToken() {
        LoginRequest request = new LoginRequest();
        request.setUsername("student1");
        request.setPassword("wrong-password");

        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("bad credentials"));

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(BadCredentialsException.class);

        verify(jwtService, never()).generateAccessToken(any());
    }
}
