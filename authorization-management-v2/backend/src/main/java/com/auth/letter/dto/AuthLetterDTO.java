package com.auth.letter.dto;

import com.auth.letter.enums.AuthLetterStatus;
import com.auth.letter.enums.OrgLevel;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Authorization Letter DTO
 */
@Data
public class AuthLetterDTO {
    private Long id;
    private String code;
    private String name;
    private String description;
    private AuthLetterStatus status;
    private OrgLevel publishLevel;
    private OrgLevel authorizedLevel;
    private LocalDateTime validFrom;
    private LocalDateTime validTo;
    private LocalDateTime publishedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<SceneDTO> scenes = new ArrayList<>();
}