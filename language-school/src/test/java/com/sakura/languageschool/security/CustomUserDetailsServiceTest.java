package com.sakura.languageschool.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.sakura.languageschool.model.Roles;
import com.sakura.languageschool.model.UserRole;
import com.sakura.languageschool.model.Users;
import com.sakura.languageschool.repository.UserDao;
import com.sakura.languageschool.repository.UserRoleDao;

class CustomUserDetailsServiceTest {

    private UserDao userDao;
    private UserRoleDao userRoleDao;
    private CustomUserDetailsService service;

    @BeforeEach
    void setUp() {
        userDao = mock(UserDao.class);
        userRoleDao = mock(UserRoleDao.class);
        service = new CustomUserDetailsService(userDao, userRoleDao);
    }

    @Test
    void loadsEnabledUserAndMapsRolesToAuthorities() {
        Users user = user("student1", "stored-password", true, 7L);
        UserRole studentRole = roleLink("STUDENT");
        UserRole alreadyPrefixedRole = roleLink("ROLE_STUDENT");

        when(userDao.findByUsername("student1"))
                .thenReturn(Optional.of(user));
        when(userRoleDao.findByUserId(7L))
                .thenReturn(List.of(studentRole, alreadyPrefixedRole));

        UserDetails details = service.loadUserByUsername("student1");

        assertThat(details.getUsername()).isEqualTo("student1");
        assertThat(details.getPassword()).isEqualTo("stored-password");
        assertThat(details.isEnabled()).isTrue();
        assertThat(details.getAuthorities())
                .extracting("authority")
                .containsExactly("ROLE_STUDENT");
    }

    @Test
    void rejectsUnknownUser() {
        when(userDao.findByUsername("missing"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.loadUserByUsername("missing"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessage("User not found: missing");

        verify(userRoleDao, org.mockito.Mockito.never()).findByUserId(7L);
    }

    @Test
    void rejectsDisabledUserBeforeLoadingRoles() {
        Users user = user("disabled", "stored-password", false, 9L);
        when(userDao.findByUsername("disabled"))
                .thenReturn(Optional.of(user));

        assertThatThrownBy(() -> service.loadUserByUsername("disabled"))
                .isInstanceOf(DisabledException.class)
                .hasMessage("User account is disabled");

        verify(userRoleDao, org.mockito.Mockito.never()).findByUserId(9L);
    }

    private static Users user(
            String username, String password, boolean enabled, Long id) {
        Users user = mock(Users.class);
        when(user.getId()).thenReturn(id);
        when(user.getUsername()).thenReturn(username);
        when(user.getPassword()).thenReturn(password);
        when(user.isEnabled()).thenReturn(enabled);
        return user;
    }

    private static UserRole roleLink(String roleName) {
        Roles role = mock(Roles.class);
        when(role.getRole()).thenReturn(roleName);

        UserRole userRole = mock(UserRole.class);
        when(userRole.getRole()).thenReturn(role);
        return userRole;
    }
}
