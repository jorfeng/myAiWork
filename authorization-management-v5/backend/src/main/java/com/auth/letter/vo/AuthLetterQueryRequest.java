package com.auth.letter.vo;

import java.util.List;

public class AuthLetterQueryRequest extends PageRequest {
    private String name;
    private List<String> authTargetLevel;
    private List<String> applicableRegion;
    private List<String> authPublishLevel;
    private List<String> authPublishOrg;
    private Integer publishYear;
    private String status;

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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}