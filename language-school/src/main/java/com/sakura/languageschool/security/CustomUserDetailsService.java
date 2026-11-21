package com.sakura.languageschool.security;

import java.util.Collection;

import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.sakura.languageschool.model.Roles;
import com.sakura.languageschool.model.UserRole;
import com.sakura.languageschool.model.Users;
import com.sakura.languageschool.repository.UserDao;
import com.sakura.languageschool.repository.UserRoleDao;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserDao userDao;
    private final UserRoleDao userRoleDao;

    public CustomUserDetailsService(UserDao userDao, UserRoleDao userRoleDao) {
        this.userDao = userDao;
        this.userRoleDao = userRoleDao;
    }

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {

        Users user = userDao.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "User not found: " + username));

        if (!user.isEnabled()) {
            throw new DisabledException("User account is disabled");
        }

        Collection<GrantedAuthority> authorities = userRoleDao
                .findByUserId(user.getId())
                .stream()
                .map(UserRole::getRole)
                .map(Roles::getRole)
                .filter(role -> role != null && !role.isBlank())
                .map(this::toAuthority)
                .distinct()
                .map(SimpleGrantedAuthority::new)
                .map(GrantedAuthority.class::cast)
                .toList();

        return User.withUsername(user.getUsername())
                .password(user.getPassword())
                .authorities(authorities)
                .disabled(!user.isEnabled())
                .build();
    }

    private String toAuthority(String role) {
        return role.startsWith("ROLE_") ? role : "ROLE_" + role;
    }
}
