package com.gamestack.auth;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationManager;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class SecurityConfigIntegrationTest {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Test
    void authenticationManagerIsPresent() {
        assertThat(authenticationManager).isNotNull();
    }
}
