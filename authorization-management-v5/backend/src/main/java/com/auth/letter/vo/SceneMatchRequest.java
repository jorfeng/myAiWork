package com.auth.letter.vo;

import java.util.Map;

public class SceneMatchRequest {
    private Long letterId;
    private Map<String, Object> params;

    public Long getLetterId() {
        return letterId;
    }

    public void setLetterId(Long letterId) {
        this.letterId = letterId;
    }

    public Map<String, Object> getParams() {
        return params;
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }
}