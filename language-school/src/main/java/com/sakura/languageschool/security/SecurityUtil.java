package com.sakura.languageschool.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtil {
	@Autowired
	PasswordEncoder encoder;
	
	public SecurityUtil(PasswordEncoder encoder) {
        this.encoder = encoder;
    }
	
	public String getHash(String plainPassword) {
		return this.encoder.encode(plainPassword);
	}
}
