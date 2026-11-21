package com.sakura.languageschool.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.http.HttpMethod;
import com.sakura.languageschool.api.common.ApiErrorResponse;
import tools.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;

@Configuration
@EnableConfigurationProperties(JwtProperties.class)
public class webConfig {
	@Bean
	public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

	@Bean
	public AuthenticationManager authenticationManager(
			AuthenticationConfiguration configuration) throws Exception {
		return configuration.getAuthenticationManager();
	}
	
	@Bean
	public SecurityFilterChain securityFilterChain(
			HttpSecurity http,
			JwtAuthenticationFilter jwtAuthenticationFilter,
			ObjectMapper objectMapper)
            throws Exception {

        http
            .csrf(csrf -> csrf.disable())
			.cors(cors -> cors.disable())
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .exceptionHandling(exceptions -> exceptions
                .authenticationEntryPoint((request, response, exception) ->
                    writeSecurityError(
                        response,
                        objectMapper,
                        HttpStatus.UNAUTHORIZED,
                        "UNAUTHORIZED",
                        "Authentication is required"))
                .accessDeniedHandler((request, response, exception) ->
                    writeSecurityError(
                        response,
                        objectMapper,
                        HttpStatus.FORBIDDEN,
                        "FORBIDDEN",
                        "You do not have permission to access this resource")))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/api/auth/register",
                    "/api/auth/login").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/auth/me").authenticated()
                .requestMatchers(HttpMethod.GET, "/api/courses", "/api/courses/*")
                    .permitAll()
                .requestMatchers(HttpMethod.GET, "/api/batches", "/api/batches/*")
                    .permitAll()
                .requestMatchers(HttpMethod.GET, "/api/admin/users", "/api/admin/users/*")
                    .hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/admin/users/*")
                    .hasRole("ADMIN")
                .requestMatchers(HttpMethod.PATCH, "/api/admin/users/*/deactivate")
                    .hasRole("ADMIN")
                .requestMatchers(HttpMethod.PATCH, "/api/admin/users/*/activate")
                    .hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/admin/students", "/api/admin/students/*")
                    .hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/admin/students/*")
                    .hasRole("ADMIN")
                .requestMatchers(HttpMethod.PATCH, "/api/admin/students/*/deactivate")
                    .hasRole("ADMIN")
                .requestMatchers(HttpMethod.PATCH, "/api/admin/students/*/activate")
                    .hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/admin/courses/*")
                    .hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/admin/batches/*")
                    .hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/admin/courses")
                    .hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/admin/courses/*")
                    .hasRole("ADMIN")
                .requestMatchers(HttpMethod.PATCH, "/api/admin/courses/*/deactivate")
                    .hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/admin/batches")
                    .hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/admin/batches/*")
                    .hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/admin/teachers")
                    .hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/admin/teachers", "/api/admin/teachers/*")
                    .hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/admin/teachers/*")
                    .hasRole("ADMIN")
                .requestMatchers(HttpMethod.PATCH, "/api/admin/teachers/*/deactivate")
                    .hasRole("ADMIN")
                .requestMatchers(HttpMethod.PATCH, "/api/admin/teachers/*/activate")
                    .hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/admin/enrollments", "/api/admin/enrollments/*")
                    .hasRole("ADMIN")
                .requestMatchers(HttpMethod.PATCH, "/api/admin/enrollments/*/approve")
                    .hasRole("ADMIN")
                .requestMatchers(HttpMethod.PATCH, "/api/admin/enrollments/*/reject")
                    .hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/teacher/batches", "/api/teacher/batches/*/students")
                    .hasRole("TEACHER")
                .requestMatchers(HttpMethod.POST, "/api/enrollments")
                    .hasRole("STUDENT")
                .requestMatchers(HttpMethod.GET, "/api/enrollments/my")
                    .hasRole("STUDENT")
                .anyRequest().denyAll())
            .addFilterBefore(
                jwtAuthenticationFilter,
                UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

	private void writeSecurityError(
			HttpServletResponse response,
			ObjectMapper objectMapper,
			HttpStatus status,
			String code,
			String message) throws java.io.IOException {
		response.setStatus(status.value());
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		objectMapper.writeValue(
				response.getOutputStream(),
				new ApiErrorResponse(status, code, message));
	}
}
