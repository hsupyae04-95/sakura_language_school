package com.sakura.languageschool;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
		"app.security.jwt.secret=01234567890123456789012345678901"
})
class LanguageSchoolApplicationTests {

	@Test
	void contextLoads() {
	}

}
