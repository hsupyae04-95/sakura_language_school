package com.sakura.languageschool.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

import javax.crypto.SecretKey;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.sakura.languageschool.security.JwtService;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@SpringBootTest(properties = {
        "app.security.jwt.secret=01234567890123456789012345678901"
})
@AutoConfigureMockMvc
class JwtSecurityIntegrationTest {

    private static final String SECRET =
            "01234567890123456789012345678901";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtService jwtService;

    @Test
    void allowsValidBearerTokenOnProtectedRoute() throws Exception {
        mockMvc.perform(get("/api/enrollments/my")
                        .header(HttpHeaders.AUTHORIZATION,
                                bearerToken("seed_student01")))
                .andExpect(status().isOk());
    }

    @Test
    void allowsPublicCourseCatalogWithoutBearerToken() throws Exception {
        mockMvc.perform(get("/api/courses"))
                .andExpect(status().isOk());
    }

    @Test
    void allowsPublicBatchCatalogWithoutBearerToken() throws Exception {
        mockMvc.perform(get("/api/batches"))
                .andExpect(status().isOk());
    }

    @Test
    void rejectsMissingBearerToken() throws Exception {
        mockMvc.perform(get("/api/admin/enrollments"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("UNAUTHORIZED"))
                .andExpect(jsonPath("$.message")
                        .value("Authentication is required"));
    }

    @Test
    void rejectsMalformedBearerToken() throws Exception {
        mockMvc.perform(get("/api/admin/enrollments")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer not-a-jwt"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void rejectsExpiredBearerToken() throws Exception {
        mockMvc.perform(get("/api/admin/enrollments")
                        .header(HttpHeaders.AUTHORIZATION,
                                "Bearer " + expiredToken()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void rejectsBearerTokenWithBadSignature() throws Exception {
        mockMvc.perform(get("/api/admin/enrollments")
                        .header(HttpHeaders.AUTHORIZATION,
                                "Bearer " + badSignatureToken()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void rejectsStudentFromAdministrativeReviewRoute() throws Exception {
        mockMvc.perform(patch("/api/admin/enrollments/1/approve")
                        .header(HttpHeaders.AUTHORIZATION,
                                bearerToken("seed_student01"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"APPROVED\"}"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("FORBIDDEN"));
    }

    @Test
    void rejectsStudentFromAdminEnrollmentList() throws Exception {
        mockMvc.perform(get("/api/admin/enrollments")
                        .header(HttpHeaders.AUTHORIZATION,
                                bearerToken("seed_student01")))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("FORBIDDEN"));
    }

    @Test
    void rejectsStudentFromTeacherAssignedBatchRoute() throws Exception {
        mockMvc.perform(get("/api/teacher/batches")
                        .header(HttpHeaders.AUTHORIZATION,
                                bearerToken("seed_student01")))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("FORBIDDEN"));
    }

    @Test
    void allowsTeacherToViewAssignedBatches() throws Exception {
        mockMvc.perform(get("/api/teacher/batches")
                        .header(HttpHeaders.AUTHORIZATION,
                                bearerToken("seed_teacher01")))
                .andExpect(status().isOk());
    }

    @Test
    void allowsAdminToViewAdminEnrollmentList() throws Exception {
        mockMvc.perform(get("/api/admin/enrollments")
                        .header(HttpHeaders.AUTHORIZATION,
                                bearerToken("seed_admin")))
                .andExpect(status().isOk());
    }

    @Test
    void rejectsStudentFromCourseCreation() throws Exception {
        mockMvc.perform(post("/api/admin/courses")
                        .header(HttpHeaders.AUTHORIZATION,
                                bearerToken("seed_student01"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Unauthorized\",\"level\":\"N5\"}"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("FORBIDDEN"));
    }

    private String bearerToken(String username) {
        return "Bearer " + jwtService.generateAccessToken(username);
    }

    private String expiredToken() {
        SecretKey key = signingKey();
        Instant expiredAt = Instant.now().minusSeconds(60);

        return Jwts.builder()
                .subject("seed_student01")
                .issuedAt(Date.from(expiredAt.minusSeconds(60)))
                .expiration(Date.from(expiredAt))
                .signWith(key)
                .compact();
    }

    private String badSignatureToken() {
        SecretKey wrongKey = Keys.hmacShaKeyFor(
                "abcdefghijklmnopqrstuvwxyz123456".getBytes(StandardCharsets.UTF_8));

        return Jwts.builder()
                .subject("seed_student01")
                .issuedAt(new Date())
                .expiration(Date.from(Instant.now().plusSeconds(3600)))
                .signWith(wrongKey)
                .compact();
    }

    private SecretKey signingKey() {
        return Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
    }
}
