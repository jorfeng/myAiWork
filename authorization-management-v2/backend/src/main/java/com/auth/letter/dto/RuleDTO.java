package com.auth.letter.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Rule DTO
 */
@Data
public class RuleDTO {
    private Long id;
    private Long sceneId;
    private String name;
    private String description;
    private Integer orderIndex;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<ConditionDTO> conditions = new ArrayList<>();
}