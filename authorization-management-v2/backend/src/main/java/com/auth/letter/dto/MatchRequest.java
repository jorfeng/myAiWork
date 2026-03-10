package com.auth.letter.dto;

import com.auth.letter.enums.OrgLevel;
import lombok.Data;

import java.util.Map;

/**
 * Match Request DTO
 */
@Data
public class MatchRequest {
    /**
     * Authorization letter code (optional, if not provided, match all)
     */
    private String authLetterCode;

    /**
     * Matching context with dynamic fields
     */
    private Map<String, Object> context;
}