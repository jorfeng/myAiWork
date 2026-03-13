package com.auth.letter.vo;

public class OperationLogQueryRequest extends PageRequest {
    private Long letterId;

    public Long getLetterId() {
        return letterId;
    }

    public void setLetterId(Long letterId) {
        this.letterId = letterId;
    }
}