package com.auth.letter.entity;

import java.time.LocalDateTime;

public class AuthLetter {
    private Long id;
    private String name;
    private String authTargetLevel;
    private String applicableRegion;
    private String authPublishLevel;
    private String authPublishOrg;
    private Integer publishYear;
    private String summary;
    private String status;
    private String createdBy;
    private LocalDateTime createdTime;
    private String updatedBy;
    private LocalDateTime updatedTime;

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

    public String getAuthTargetLevel() {
        return authTargetLevel;
    }

    public void setAuthTargetLevel(String authTargetLevel) {
        this.authTargetLevel = authTargetLevel;
    }

    public String getApplicableRegion() {
        return applicableRegion;
    }

    public void setApplicableRegion(String applicableRegion) {
        this.applicableRegion = applicableRegion;
    }

    public String getAuthPublishLevel() {
        return authPublishLevel;
    }

    public void setAuthPublishLevel(String authPublishLevel) {
        this.authPublishLevel = authPublishLevel;
    }

    public String getAuthPublishOrg() {
        return authPublishOrg;
    }

    public void setAuthPublishOrg(String authPublishOrg) {
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
}