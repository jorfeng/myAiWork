package com.auth.letter.dto;

import com.auth.letter.enums.LogicOperator;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Condition DTO
 */
@Data
public class ConditionDTO {
    private Long id;
    private Long ruleId;
    private Long parentId;
    private LogicOperator logicOperator;
    private String fieldName;
    private String operator;
    private String value;
    private LocalDateTime createdAt;
    private List<ConditionDTO> children = new ArrayList<>();
}