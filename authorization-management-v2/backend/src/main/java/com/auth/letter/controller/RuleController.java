package com.auth.letter.controller;

import com.auth.letter.dto.ApiResponse;
import com.auth.letter.dto.RuleDTO;
import com.auth.letter.service.RuleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Rule Controller
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class RuleController {

    private final RuleService ruleService;

    /**
     * Get rules by scene ID
     */
    @GetMapping("/scenes/{sceneId}/rules")
    public ApiResponse<List<RuleDTO>> getRulesBySceneId(@PathVariable Long sceneId) {
        return ApiResponse.success(ruleService.getRulesBySceneId(sceneId));
    }

    /**
     * Get rule by ID
     */
    @GetMapping("/rules/{id}")
    public ApiResponse<RuleDTO> getRule(@PathVariable Long id) {
        return ApiResponse.success(ruleService.getRule(id));
    }

    /**
     * Create rule
     */
    @PostMapping("/scenes/{sceneId}/rules")
    public ApiResponse<RuleDTO> createRule(@PathVariable Long sceneId, @Valid @RequestBody RuleDTO dto) {
        return ApiResponse.success("Rule created successfully", ruleService.createRule(sceneId, dto));
    }

    /**
     * Update rule
     */
    @PutMapping("/rules/{id}")
    public ApiResponse<RuleDTO> updateRule(@PathVariable Long id, @Valid @RequestBody RuleDTO dto) {
        return ApiResponse.success("Rule updated successfully", ruleService.updateRule(id, dto));
    }

    /**
     * Delete rule
     */
    @DeleteMapping("/rules/{id}")
    public ApiResponse<Void> deleteRule(@PathVariable Long id) {
        ruleService.deleteRule(id);
        return ApiResponse.success("Rule deleted successfully", null);
    }
}