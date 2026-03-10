package com.auth.letter.controller;

import com.auth.letter.dto.ApiResponse;
import com.auth.letter.dto.SceneDTO;
import com.auth.letter.service.SceneService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Scene Controller
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class SceneController {

    private final SceneService sceneService;

    /**
     * Get scenes by auth letter ID
     */
    @GetMapping("/auth-letters/{authLetterId}/scenes")
    public ApiResponse<List<SceneDTO>> getScenesByAuthLetterId(@PathVariable Long authLetterId) {
        return ApiResponse.success(sceneService.getScenesByAuthLetterId(authLetterId));
    }

    /**
     * Get scene by ID
     */
    @GetMapping("/scenes/{id}")
    public ApiResponse<SceneDTO> getScene(@PathVariable Long id) {
        return ApiResponse.success(sceneService.getScene(id));
    }

    /**
     * Create scene
     */
    @PostMapping("/auth-letters/{authLetterId}/scenes")
    public ApiResponse<SceneDTO> createScene(@PathVariable Long authLetterId, @Valid @RequestBody SceneDTO dto) {
        return ApiResponse.success("Scene created successfully", sceneService.createScene(authLetterId, dto));
    }

    /**
     * Update scene
     */
    @PutMapping("/scenes/{id}")
    public ApiResponse<SceneDTO> updateScene(@PathVariable Long id, @Valid @RequestBody SceneDTO dto) {
        return ApiResponse.success("Scene updated successfully", sceneService.updateScene(id, dto));
    }

    /**
     * Delete scene
     */
    @DeleteMapping("/scenes/{id}")
    public ApiResponse<Void> deleteScene(@PathVariable Long id) {
        sceneService.deleteScene(id);
        return ApiResponse.success("Scene deleted successfully", null);
    }
}