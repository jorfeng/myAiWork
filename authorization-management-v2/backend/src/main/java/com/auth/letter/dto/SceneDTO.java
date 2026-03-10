package com.auth.letter.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Scene DTO
 */
@Data
public class SceneDTO {
    private Long id;
    private Long authLetterId;
    private String name;
    private String description;
    private Integer decisionLevel;
    private Integer orderIndex;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<RuleDTO> rules = new ArrayList<>();
}