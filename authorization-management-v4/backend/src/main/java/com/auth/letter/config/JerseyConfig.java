package com.auth.letter.config;

import com.auth.letter.api.impl.*;
import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JerseyConfig extends ResourceConfig {

    public JerseyConfig() {
        // 直接注册资源类
        register(AuthLetterApiImpl.class);
        register(RuleParamApiImpl.class);
        register(LookupApiImpl.class);
        register(SceneApiImpl.class);
        register(AttachmentApiImpl.class);
    }
}