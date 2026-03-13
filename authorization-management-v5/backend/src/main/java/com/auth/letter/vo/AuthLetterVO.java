package com.auth.letter.vo;

import java.time.LocalDateTime;
import java.util.List;

public class AuthLetterVO {
    private Long id;
    private String name;
    private List<String> authTargetLevel;
    private List<String> applicableRegion;
    private List<String> authPublishLevel;
    private List<String> authPublishOrg;
    private Integer publishYear;
    private String summary;
    private String status;
    private String createdBy;
    private LocalDateTime createdTime;
    private String updatedBy;
    private LocalDateTime updatedTime;

    // 用于列表显示的标签文本
    private String authTargetLevelLabel;
    private String applicableRegionLabel;
    private String authPublishLevelLabel;
    private String authPublishOrgLabel;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getAuthTargetLevel() {
        return authTargetLevel;
    }

    public void setAuthTargetLevel(List<String> authTargetLevel) {
        this.authTargetLevel = authTargetLevel;
    }

    public List<String> getApplicableRegion() {
        return applicableRegion;
    }

    public void setApplicableRegion(List<String> applicableRegion) {
        this.applicableRegion = applicableRegion;
    }

    public List<String> getAuthPublishLevel() {
        return authPublishLevel;
    }

    public void setAuthPublishLevel(List<String> authPublishLevel) {
        this.authPublishLevel = authPublishLevel;
    }

    public List<String> getAuthPublishOrg() {
        return authPublishOrg;
    }

    public void setAuthPublishOrg(List<String> authPublishOrg) {
        this.authPublishOrg = authPublishOrg;
    }

    public Integer getPublishYear() {
        return publishYear;
    }

    public void setPublishYear(Integer publishYear) {
        this.publishYear = publishYear;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDateTime getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(LocalDateTime createdTime) {
        this.createdTime = createdTime;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public LocalDateTime getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(LocalDateTime updatedTime) {
        this.updatedTime = updatedTime;
    }

    public String getAuthTargetLevelLabel() {
        return authTargetLevelLabel;
    }

    public void setAuthTargetLevelLabel(String authTargetLevelLabel) {
        this.authTargetLevelLabel = authTargetLevelLabel;
    }

    public String getApplicableRegionLabel() {
        return applicableRegionLabel;
    }

    public void setApplicableRegionLabel(String applicableRegionLabel) {
        this.applicableRegionLabel = applicableRegionLabel;
    }

    public String getAuthPublishLevelLabel() {
        return authPublishLevelLabel;
    }

    public void setAuthPublishLevelLabel(String authPublishLevelLabel) {
        this.authPublishLevelLabel = authPublishLevelLabel;
    }

    public String getAuthPublishOrgLabel() {
        return authPublishOrgLabel;
    }

    public void setAuthPublishOrgLabel(String authPublishOrgLabel) {
        this.authPublishOrgLabel = authPublishOrgLabel;
    }
}