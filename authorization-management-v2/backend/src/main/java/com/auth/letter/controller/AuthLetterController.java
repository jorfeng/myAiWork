package com.auth.letter.controller;

import com.auth.letter.dto.ApiResponse;
import com.auth.letter.dto.AuthLetterDTO;
import com.auth.letter.dto.MatchRequest;
import com.auth.letter.dto.MatchResult;
import com.auth.letter.enums.OrgLevel;
import com.auth.letter.service.AuthLetterService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Authorization Letter Controller
 */
@RestController
@RequestMapping("/api/auth-letters")
@RequiredArgsConstructor
public class AuthLetterController {

    private final AuthLetterService authLetterService;

    /**
     * Get all authorization letters
     */
    @GetMapping
    public ApiResponse<List<AuthLetterDTO>> getAllAuthLetters() {
        return ApiResponse.success(authLetterService.getAllAuthLetters());
    }

    /**
     * Get authorization letter by ID
     */
    @GetMapping("/{id}")
    public ApiResponse<AuthLetterDTO> getAuthLetter(@PathVariable Long id) {
        return ApiResponse.success(authLetterService.getAuthLetter(id));
    }

    /**
     * Get authorization letter by code
     */
    @GetMapping("/code/{code}")
    public ApiResponse<AuthLetterDTO> getAuthLetterByCode(@PathVariable String code) {
        return ApiResponse.success(authLetterService.getAuthLetterByCode(code));
    }

    /**
     * Create authorization letter
     */
    @PostMapping
    public ApiResponse<AuthLetterDTO> createAuthLetter(@Valid @RequestBody AuthLetterDTO dto) {
        return ApiResponse.success("Authorization letter created successfully", authLetterService.createAuthLetter(dto));
    }

    /**
     * Update authorization letter
     */
    @PutMapping("/{id}")
    public ApiResponse<AuthLetterDTO> updateAuthLetter(@PathVariable Long id, @Valid @RequestBody AuthLetterDTO dto) {
        return ApiResponse.success("Authorization letter updated successfully", authLetterService.updateAuthLetter(id, dto));
    }

    /**
     * Delete authorization letter
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteAuthLetter(@PathVariable Long id) {
        authLetterService.deleteAuthLetter(id);
        return ApiResponse.success("Authorization letter deleted successfully", null);
    }

    /**
     * Publish authorization letter
     */
    @PostMapping("/{id}/publish")
    public ApiResponse<AuthLetterDTO> publishAuthLetter(
            @PathVariable Long id,
            @RequestParam(required = false) OrgLevel publishLevel,
            @RequestParam(required = false) OrgLevel authorizedLevel,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime validFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime validTo) {
        return ApiResponse.success("Authorization letter published successfully",
                authLetterService.publishAuthLetter(id, publishLevel, authorizedLevel, validFrom, validTo));
    }

    /**
     * Match authorization letters
     */
    @PostMapping("/match")
    public ApiResponse<List<MatchResult>> match(@RequestBody MatchRequest request) {
        return ApiResponse.success(authLetterService.match(request));
    }
}