package com.auth.letter.service;

import com.auth.letter.dto.ConditionDTO;
import com.auth.letter.dto.RuleDTO;
import com.auth.letter.entity.Condition;
import com.auth.letter.entity.Rule;
import com.auth.letter.entity.Scene;
import com.auth.letter.enums.AuthLetterStatus;
import com.auth.letter.exception.BusinessException;
import com.auth.letter.repository.ConditionRepository;
import com.auth.letter.repository.RuleRepository;
import com.auth.letter.repository.SceneRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Rule Service
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RuleService {

    private final RuleRepository ruleRepository;
    private final SceneRepository sceneRepository;
    private final ConditionRepository conditionRepository;

    /**
     * Create rule
     */
    @Transactional
    public RuleDTO createRule(Long sceneId, RuleDTO dto) {
        Scene scene = sceneRepository.findById(sceneId)
                .orElseThrow(() -> new BusinessException("Scene not found: " + sceneId));

        if (scene.getAuthLetter().getStatus() != AuthLetterStatus.DRAFT) {
            throw new BusinessException("Can only add rule to scene in DRAFT authorization letter");
        }

        Rule rule = new Rule();
        rule.setScene(scene);
        rule.setName(dto.getName());
        rule.setDescription(dto.getDescription());
        rule.setOrderIndex(dto.getOrderIndex() != null ? dto.getOrderIndex() : 0);

        Rule saved = ruleRepository.save(rule);

        // Create conditions if provided
        if (dto.getConditions() != null && !dto.getConditions().isEmpty()) {
            for (ConditionDTO conditionDTO : dto.getConditions()) {
                createCondition(saved, null, conditionDTO);
            }
        }

        return toDTO(saved);
    }

    /**
     * Update rule
     */
    @Transactional
    public RuleDTO updateRule(Long id, RuleDTO dto) {
        Rule rule = ruleRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Rule not found: " + id));

        if (rule.getScene().getAuthLetter().getStatus() != AuthLetterStatus.DRAFT) {
            throw new BusinessException("Can only update rule in DRAFT authorization letter");
        }

        rule.setName(dto.getName());
        rule.setDescription(dto.getDescription());
        if (dto.getOrderIndex() != null) {
            rule.setOrderIndex(dto.getOrderIndex());
        }

        Rule saved = ruleRepository.save(rule);
        return toDTO(saved);
    }

    /**
     * Delete rule
     */
    @Transactional
    public void deleteRule(Long id) {
        Rule rule = ruleRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Rule not found: " + id));

        if (rule.getScene().getAuthLetter().getStatus() != AuthLetterStatus.DRAFT) {
            throw new BusinessException("Can only delete rule from DRAFT authorization letter");
        }

        ruleRepository.delete(rule);
    }

    /**
     * Get rule by ID
     */
    @Transactional(readOnly = true)
    public RuleDTO getRule(Long id) {
        Rule rule = ruleRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Rule not found: " + id));
        return toDTO(rule);
    }

    /**
     * Get rules by scene ID
     */
    @Transactional(readOnly = true)
    public List<RuleDTO> getRulesBySceneId(Long sceneId) {
        return ruleRepository.findBySceneIdOrderByOrderIndexAsc(sceneId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Create condition recursively
     */
    private Condition createCondition(Rule rule, Condition parent, ConditionDTO dto) {
        Condition condition = new Condition();
        condition.setRule(rule);
        condition.setParent(parent);
        condition.setLogicOperator(dto.getLogicOperator());
        condition.setFieldName(dto.getFieldName());
        condition.setOperator(dto.getOperator());
        condition.setValue(dto.getValue());

        Condition saved = conditionRepository.save(condition);

        // Create child conditions if provided
        if (dto.getChildren() != null && !dto.getChildren().isEmpty()) {
            for (ConditionDTO childDTO : dto.getChildren()) {
                createCondition(rule, saved, childDTO);
            }
        }

        return saved;
    }

    /**
     * Convert entity to DTO
     */
    private RuleDTO toDTO(Rule entity) {
        RuleDTO dto = new RuleDTO();
        dto.setId(entity.getId());
        dto.setSceneId(entity.getScene().getId());
        dto.setName(entity.getName());
        dto.setDescription(entity.getDescription());
        dto.setOrderIndex(entity.getOrderIndex());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());

        List<Condition> conditions = conditionRepository.findByRuleIdAndParentIdIsNull(entity.getId());
        dto.setConditions(conditions.stream().map(this::toDTO).collect(Collectors.toList()));

        return dto;
    }

    /**
     * Convert Condition entity to DTO
     */
    private ConditionDTO toDTO(Condition entity) {
        ConditionDTO dto = new ConditionDTO();
        dto.setId(entity.getId());
        dto.setRuleId(entity.getRule() != null ? entity.getRule().getId() : null);
        dto.setParentId(entity.getParent() != null ? entity.getParent().getId() : null);
        dto.setLogicOperator(entity.getLogicOperator());
        dto.setFieldName(entity.getFieldName());
        dto.setOperator(entity.getOperator());
        dto.setValue(entity.getValue());
        dto.setCreatedAt(entity.getCreatedAt());

        if (entity.getChildren() != null && !entity.getChildren().isEmpty()) {
            dto.setChildren(entity.getChildren().stream().map(this::toDTO).collect(Collectors.toList()));
        }

        return dto;
    }
}