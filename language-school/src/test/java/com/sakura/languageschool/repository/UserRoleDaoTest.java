package com.sakura.languageschool.repository;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.sakura.languageschool.model.Users;

@SpringBootTest(properties = {
        "app.security.jwt.secret=01234567890123456789012345678901"
})
class UserRoleDaoTest {

    @Autowired
    private UserDao userDao;

    @Autowired
    private UserRoleDao userRoleDao;

    @Test
    void findsRolesThroughEmbeddedUserRoleId() {
        Users user = userDao.findByUsername("seed_student01")
                .orElseThrow();

        assertThat(userRoleDao.findByUserId(user.getId()))
                .extracting(userRole -> userRole.getRole().getRole())
                .containsExactly("STUDENT");
    }
}
