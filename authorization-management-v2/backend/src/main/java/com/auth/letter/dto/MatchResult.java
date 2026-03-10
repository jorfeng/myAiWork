package com.auth.letter.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Match Result DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MatchResult {
    private Long authLetterId;
    private String authLetterCode;
    private String authLetterName;
    private Long sceneId;
    private String sceneName;
    private Integer decisionLevel;
}