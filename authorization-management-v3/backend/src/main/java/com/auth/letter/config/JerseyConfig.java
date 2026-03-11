package com.auth.letter.config;

import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.context.annotation.Configuration;

/**
 * Jersey Configuration for JAX-RS
 */
@Configuration
public class JerseyConfig extends ResourceConfig {

    public JerseyConfig() {
        // Register packages containing JAX-RS resources
        packages("com.auth.letter.controller");
    }
}