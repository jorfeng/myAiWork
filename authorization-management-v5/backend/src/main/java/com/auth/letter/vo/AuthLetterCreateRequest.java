package com.auth.letter.vo;

import java.util.List;

public class AuthLetterCreateRequest {
    private Long id;
    private String name;
    private List<String> authTargetLevel;
    private List<String> applicableRegion;
    private List<String> authPublishLevel;
    private List<String> authPublishOrg;
    private Integer publishYear;
    private String summary;

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
}