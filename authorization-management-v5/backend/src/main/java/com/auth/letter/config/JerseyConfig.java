package com.auth.letter.config;

import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.stereotype.Component;
import com.auth.letter.api.impl.AuthLetterApiImpl;
import com.auth.letter.api.impl.AttachmentApiImpl;
import com.auth.letter.api.impl.SceneApiImpl;
import com.auth.letter.api.impl.RuleParamApiImpl;
import com.auth.letter.api.impl.LookupApiImpl;
import com.auth.letter.api.impl.OperationLogApiImpl;

@Component
public class JerseyConfig extends ResourceConfig {
    public JerseyConfig() {
        register(AuthLetterApiImpl.class);
        register(AttachmentApiImpl.class);
        register(SceneApiImpl.class);
        register(RuleParamApiImpl.class);
        register(LookupApiImpl.class);
        register(OperationLogApiImpl.class);
    }
}