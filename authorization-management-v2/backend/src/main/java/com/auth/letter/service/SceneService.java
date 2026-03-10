package com.auth.letter.service;

import com.auth.letter.dto.ConditionDTO;
import com.auth.letter.dto.SceneDTO;
import com.auth.letter.entity.AuthLetter;
import com.auth.letter.entity.Condition;
import com.auth.letter.entity.Rule;
import com.auth.letter.entity.Scene;
import com.auth.letter.enums.AuthLetterStatus;
import com.auth.letter.exception.BusinessException;
import com.auth.letter.repository.AuthLetterRepository;
import com.auth.letter.repository.ConditionRepository;
import com.auth.letter.repository.RuleRepository;
import com.auth.letter.repository.SceneRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Scene Service
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SceneService {

    private final SceneRepository sceneRepository;
    private final AuthLetterRepository authLetterRepository;
    private final RuleRepository ruleRepository;
    private final ConditionRepository conditionRepository;

    /**
     * Create scene
     */
    @Transactional
    public SceneDTO createScene(Long authLetterId, SceneDTO dto) {
        AuthLetter authLetter = authLetterRepository.findById(authLetterId)
                .orElseThrow(() -> new BusinessException("Authorization letter not found: " + authLetterId));

        if (authLetter.getStatus() != AuthLetterStatus.DRAFT) {
            throw new BusinessException("Can only add scene to DRAFT authorization letter");
        }

        Scene scene = new Scene();
        scene.setAuthLetter(authLetter);
        scene.setName(dto.getName());
        scene.setDescription(dto.getDescription());
        scene.setDecisionLevel(dto.getDecisionLevel());
        scene.setOrderIndex(dto.getOrderIndex() != null ? dto.getOrderIndex() : 0);

        Scene saved = sceneRepository.save(scene);
        return toDTO(saved);
    }

    /**
     * Update scene
     */
    @Transactional
    public SceneDTO updateScene(Long id, SceneDTO dto) {
        Scene scene = sceneRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Scene not found: " + id));

        if (scene.getAuthLetter().getStatus() != AuthLetterStatus.DRAFT) {
            throw new BusinessException("Can only update scene in DRAFT authorization letter");
        }

        scene.setName(dto.getName());
        scene.setDescription(dto.getDescription());
        scene.setDecisionLevel(dto.getDecisionLevel());
        if (dto.getOrderIndex() != null) {
            scene.setOrderIndex(dto.getOrderIndex());
        }

        Scene saved = sceneRepository.save(scene);
        return toDTO(saved);
    }

    /**
     * Delete scene
     */
    @Transactional
    public void deleteScene(Long id) {
        Scene scene = sceneRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Scene not found: " + id));

        if (scene.getAuthLetter().getStatus() != AuthLetterStatus.DRAFT) {
            throw new BusinessException("Can only delete scene from DRAFT authorization letter");
        }

        sceneRepository.delete(scene);
    }

    /**
     * Get scene by ID
     */
    @Transactional(readOnly = true)
    public SceneDTO getScene(Long id) {
        Scene scene = sceneRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Scene not found: " + id));
        return toDTO(scene);
    }

    /**
     * Get scenes by auth letter ID
     */
    @Transactional(readOnly = true)
    public List<SceneDTO> getScenesByAuthLetterId(Long authLetterId) {
        return sceneRepository.findByAuthLetterIdOrderByOrderIndexAsc(authLetterId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Convert entity to DTO
     */
    private SceneDTO toDTO(Scene entity) {
        SceneDTO dto = new SceneDTO();
        dto.setId(entity.getId());
        dto.setAuthLetterId(entity.getAuthLetter().getId());
        dto.setName(entity.getName());
        dto.setDescription(entity.getDescription());
        dto.setDecisionLevel(entity.getDecisionLevel());
        dto.setOrderIndex(entity.getOrderIndex());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());

        List<Rule> rules = ruleRepository.findBySceneIdOrderByOrderIndexAsc(entity.getId());
        dto.setRules(rules.stream().map(this::toDTO).collect(Collectors.toList()));

        return dto;
    }

    /**
     * Convert Rule entity to DTO
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