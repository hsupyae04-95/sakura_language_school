package com.sakura.languageschool.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(properties = {
        "app.security.jwt.secret=01234567890123456789012345678901"
})
@AutoConfigureMockMvc
class AuthLoginIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void authenticatesSeededUserAndReturnsJwtResponse() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "seed_student01",
                                  "password": "SakuraDemo123!"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("SUCCESS"))
                .andExpect(jsonPath("$.data.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.data.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.data.expiresIn").value(3600));
    }

    @Test
    void rejectsWrongPassword() throws Exception {
        assertUnauthorized("seed_student01", "wrong-password");
    }

    @Test
    void rejectsUnknownUser() throws Exception {
        assertUnauthorized("does-not-exist", "SakuraDemo123!");
    }

    @Test
    void rejectsDisabledUser() throws Exception {
        assertUnauthorized("student", "anything");
    }

    @Test
    void rejectsInvalidLoginPayload() throws Exception {
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

    private void assertUnauthorized(String username, String password)
            throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "%s",
                                  "password": "%s"
                                }
                                """.formatted(username, password)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code")
                        .value("AUTHENTICATION_FAILED"))
                .andExpect(jsonPath("$.message")
                        .value("Invalid username or password"));
    }
}
