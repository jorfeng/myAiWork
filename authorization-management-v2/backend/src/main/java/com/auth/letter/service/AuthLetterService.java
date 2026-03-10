package com.auth.letter.service;

import com.auth.letter.dto.*;
import com.auth.letter.entity.AuthLetter;
import com.auth.letter.entity.Condition;
import com.auth.letter.entity.Rule;
import com.auth.letter.entity.Scene;
import com.auth.letter.enums.AuthLetterStatus;
import com.auth.letter.enums.OrgLevel;
import com.auth.letter.exception.BusinessException;
import com.auth.letter.repository.AuthLetterRepository;
import com.auth.letter.repository.ConditionRepository;
import com.auth.letter.repository.RuleRepository;
import com.auth.letter.repository.SceneRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Authorization Letter Service
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthLetterService {

    private final AuthLetterRepository authLetterRepository;
    private final SceneRepository sceneRepository;
    private final RuleRepository ruleRepository;
    private final ConditionRepository conditionRepository;
    private final ObjectMapper objectMapper;

    /**
     * Create authorization letter
     */
    @Transactional
    public AuthLetterDTO createAuthLetter(AuthLetterDTO dto) {
        // Check if code already exists
        if (authLetterRepository.existsByCode(dto.getCode())) {
            throw new BusinessException("Authorization letter code already exists: " + dto.getCode());
        }

        AuthLetter authLetter = new AuthLetter();
        authLetter.setCode(dto.getCode());
        authLetter.setName(dto.getName());
        authLetter.setDescription(dto.getDescription());
        authLetter.setStatus(AuthLetterStatus.DRAFT);

        AuthLetter saved = authLetterRepository.save(authLetter);
        return toDTO(saved);
    }

    /**
     * Update authorization letter (only DRAFT status)
     */
    @Transactional
    public AuthLetterDTO updateAuthLetter(Long id, AuthLetterDTO dto) {
        AuthLetter authLetter = authLetterRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Authorization letter not found: " + id));

        if (authLetter.getStatus() != AuthLetterStatus.DRAFT) {
            throw new BusinessException("Only DRAFT authorization letter can be updated");
        }

        // Check if code already exists (if code changed)
        if (!authLetter.getCode().equals(dto.getCode()) && authLetterRepository.existsByCode(dto.getCode())) {
            throw new BusinessException("Authorization letter code already exists: " + dto.getCode());
        }

        authLetter.setCode(dto.getCode());
        authLetter.setName(dto.getName());
        authLetter.setDescription(dto.getDescription());

        AuthLetter saved = authLetterRepository.save(authLetter);
        return toDTO(saved);
    }

    /**
     * Delete authorization letter (only DRAFT status)
     */
    @Transactional
    public void deleteAuthLetter(Long id) {
        AuthLetter authLetter = authLetterRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Authorization letter not found: " + id));

        if (authLetter.getStatus() != AuthLetterStatus.DRAFT) {
            throw new BusinessException("Only DRAFT authorization letter can be deleted");
        }

        authLetterRepository.delete(authLetter);
    }

    /**
     * Publish authorization letter
     */
    @Transactional
    public AuthLetterDTO publishAuthLetter(Long id, OrgLevel publishLevel, OrgLevel authorizedLevel,
                                            LocalDateTime validFrom, LocalDateTime validTo) {
        AuthLetter authLetter = authLetterRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Authorization letter not found: " + id));

        if (authLetter.getStatus() != AuthLetterStatus.DRAFT) {
            throw new BusinessException("Only DRAFT authorization letter can be published");
        }

        // Validate level hierarchy
        if (publishLevel != null && authorizedLevel != null) {
            if (!publishLevel.isHigherOrEqualTo(authorizedLevel)) {
                throw new BusinessException("Publish level must be higher than or equal to authorized level");
            }
        }

        // Check if there are scenes
        if (sceneRepository.countByAuthLetterId(id) == 0) {
            throw new BusinessException("Authorization letter must have at least one scene");
        }

        authLetter.setPublishLevel(publishLevel);
        authLetter.setAuthorizedLevel(authorizedLevel);
        authLetter.setValidFrom(validFrom);
        authLetter.setValidTo(validTo);
        authLetter.setStatus(AuthLetterStatus.PUBLISHED);
        authLetter.setPublishedAt(LocalDateTime.now());

        AuthLetter saved = authLetterRepository.save(authLetter);
        return toDTO(saved);
    }

    /**
     * Get authorization letter by ID
     */
    @Transactional(readOnly = true)
    public AuthLetterDTO getAuthLetter(Long id) {
        AuthLetter authLetter = authLetterRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Authorization letter not found: " + id));
        return toDTO(authLetter);
    }

    /**
     * Get authorization letter by code
     */
    @Transactional(readOnly = true)
    public AuthLetterDTO getAuthLetterByCode(String code) {
        AuthLetter authLetter = authLetterRepository.findByCode(code)
                .orElseThrow(() -> new BusinessException("Authorization letter not found: " + code));
        return toDTO(authLetter);
    }

    /**
     * Get all authorization letters (paged)
     */
    @Transactional(readOnly = true)
    public List<AuthLetterDTO> getAllAuthLetters() {
        return authLetterRepository.findAll().stream()
                .map(this::toDTOSimple)
                .collect(Collectors.toList());
    }

    /**
     * Match authorization letters
     */
    @Transactional(readOnly = true)
    public List<MatchResult> match(MatchRequest request) {
        List<AuthLetter> authLetters;

        if (request.getAuthLetterCode() != null && !request.getAuthLetterCode().isEmpty()) {
            // Match specific authorization letter
            authLetters = authLetterRepository
                    .findValidAuthLetterByCode(request.getAuthLetterCode(), LocalDateTime.now())
                    .map(List::of)
                    .orElse(List.of());
        } else {
            // Match all valid authorization letters
            authLetters = authLetterRepository.findValidAuthLetters(LocalDateTime.now());
        }

        List<MatchResult> results = new ArrayList<>();

        for (AuthLetter authLetter : authLetters) {
            List<Scene> scenes = sceneRepository.findByAuthLetterIdOrderByOrderIndexAsc(authLetter.getId());

            for (Scene scene : scenes) {
                if (matchScene(scene, request.getContext())) {
                    results.add(MatchResult.builder()
                            .authLetterId(authLetter.getId())
                            .authLetterCode(authLetter.getCode())
                            .authLetterName(authLetter.getName())
                            .sceneId(scene.getId())
                            .sceneName(scene.getName())
                            .decisionLevel(scene.getDecisionLevel())
                            .build());
                }
            }
        }

        return results;
    }

    /**
     * Match a scene against context
     */
    private boolean matchScene(Scene scene, Map<String, Object> context) {
        List<Rule> rules = ruleRepository.findBySceneIdOrderByOrderIndexAsc(scene.getId());

        if (rules.isEmpty()) {
            // If no rules, scene matches
            return true;
        }

        // Any rule matches means scene matches
        for (Rule rule : rules) {
            if (matchRule(rule, context)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Match a rule against context
     */
    private boolean matchRule(Rule rule, Map<String, Object> context) {
        List<Condition> rootConditions = conditionRepository.findByRuleIdAndParentIdIsNull(rule.getId());

        if (rootConditions.isEmpty()) {
            // If no conditions, rule matches
            return true;
        }

        return evaluateConditions(rootConditions, context);
    }

    /**
     * Evaluate conditions recursively
     */
    private boolean evaluateConditions(List<Condition> conditions, Map<String, Object> context) {
        if (conditions.isEmpty()) {
            return true;
        }

        if (conditions.size() == 1) {
            return evaluateCondition(conditions.get(0), context);
        }

        // For multiple conditions, evaluate based on logic operator
        Condition firstCondition = conditions.get(0);
        boolean result = evaluateCondition(firstCondition, context);

        for (int i = 1; i < conditions.size(); i++) {
            Condition condition = conditions.get(i);
            boolean conditionResult = evaluateCondition(condition, context);

            // Use the logic operator from the current condition
            if (condition.getLogicOperator() != null) {
                switch (condition.getLogicOperator()) {
                    case AND:
                        result = result && conditionResult;
                        break;
                    case OR:
                        result = result || conditionResult;
                        break;
                }
            } else {
                // Default to AND
                result = result && conditionResult;
            }
        }

        return result;
    }

    /**
     * Evaluate a single condition
     */
    private boolean evaluateCondition(Condition condition, Map<String, Object> context) {
        if (condition.isGroup()) {
            // Nested condition group
            return evaluateConditions(condition.getChildren(), context);
        }

        // Simple condition
        String fieldName = condition.getFieldName();
        String operator = condition.getOperator();
        String value = condition.getValue();

        if (fieldName == null || operator == null || value == null) {
            return true;
        }

        Object contextValue = context.get(fieldName);
        if (contextValue == null) {
            return false;
        }

        return evaluateSimpleCondition(contextValue, operator, value);
    }

    /**
     * Evaluate simple condition
     */
    private boolean evaluateSimpleCondition(Object contextValue, String operator, String value) {
        String contextStr = String.valueOf(contextValue);

        try {
            switch (operator.toUpperCase()) {
                case "EQ":
                    return contextStr.equals(value);
                case "NE":
                    return !contextStr.equals(value);
                case "GT":
                    return Double.parseDouble(contextStr) > Double.parseDouble(value);
                case "GE":
                    return Double.parseDouble(contextStr) >= Double.parseDouble(value);
                case "LT":
                    return Double.parseDouble(contextStr) < Double.parseDouble(value);
                case "LE":
                    return Double.parseDouble(contextStr) <= Double.parseDouble(value);
                case "IN":
                    List<String> inValues = objectMapper.readValue(value, new TypeReference<List<String>>() {});
                    return inValues.contains(contextStr);
                case "NOT_IN":
                    List<String> notInValues = objectMapper.readValue(value, new TypeReference<List<String>>() {});
                    return !notInValues.contains(contextStr);
                case "LIKE":
                    return contextStr.contains(value);
                default:
                    return false;
            }
        } catch (NumberFormatException e) {
            log.warn("Number format error: {}", e.getMessage());
            return false;
        } catch (JsonProcessingException e) {
            log.warn("JSON parsing error: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Check and update expired authorization letters
     */
    @Transactional
    public int checkAndExpireAuthLetters() {
        List<AuthLetter> expiredLetters = authLetterRepository.findExpiredAuthLetters(
                AuthLetterStatus.PUBLISHED, LocalDateTime.now());

        for (AuthLetter letter : expiredLetters) {
            letter.setStatus(AuthLetterStatus.EXPIRED);
            authLetterRepository.save(letter);
        }

        return expiredLetters.size();
    }

    /**
     * Convert entity to DTO (simple, without scenes)
     */
    private AuthLetterDTO toDTOSimple(AuthLetter entity) {
        AuthLetterDTO dto = new AuthLetterDTO();
        dto.setId(entity.getId());
        dto.setCode(entity.getCode());
        dto.setName(entity.getName());
        dto.setDescription(entity.getDescription());
        dto.setStatus(entity.getStatus());
        dto.setPublishLevel(entity.getPublishLevel());
        dto.setAuthorizedLevel(entity.getAuthorizedLevel());
        dto.setValidFrom(entity.getValidFrom());
        dto.setValidTo(entity.getValidTo());
        dto.setPublishedAt(entity.getPublishedAt());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }

    /**
     * Convert entity to DTO (with scenes)
     */
    private AuthLetterDTO toDTO(AuthLetter entity) {
        AuthLetterDTO dto = toDTOSimple(entity);

        List<Scene> scenes = sceneRepository.findByAuthLetterIdOrderByOrderIndexAsc(entity.getId());
        dto.setScenes(scenes.stream().map(this::toDTO).collect(Collectors.toList()));

        return dto;
    }

    /**
     * Convert Scene entity to DTO
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