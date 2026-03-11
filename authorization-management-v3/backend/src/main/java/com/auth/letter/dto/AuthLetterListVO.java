package com.auth.letter.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Authorization Letter List View Object
 */
@Data
public class AuthLetterListVO {
    private Long id;
    private String code;
    private String name;
    private String status;
    private String statusText;
    private List<String> authTargetLevel;
    private List<String> authTargetLevelText;
    private List<String> applicableRegion;
    private List<String> applicableRegionText;
    private List<String> authPublishLevel;
    private List<String> authPublishLevelText;
    private List<String> authPublishOrg;
    private List<String> authPublishOrgText;
    private Integer publishYear;
    private String createdBy;
    private LocalDateTime createdAt;
}