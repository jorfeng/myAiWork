package com.auth.letter.vo;

import java.time.LocalDateTime;
import java.util.List;

public class AuthRuleParamVO {
    private Long id;
    private String name;
    private String nameEn;
    private List<BusinessObjectVO> businessObjects;
    private List<String> valueLogics;
    private Boolean isActive;
    private String dataType;
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

    public String getNameEn() {
        return nameEn;
    }

    public void setNameEn(String nameEn) {
        this.nameEn = nameEn;
    }

    public List<BusinessObjectVO> getBusinessObjects() {
        return businessObjects;
    }

    public void setBusinessObjects(List<BusinessObjectVO> businessObjects) {
        this.businessObjects = businessObjects;
    }

    public List<String> getValueLogics() {
        return valueLogics;
    }

    public void setValueLogics(List<String> valueLogics) {
        this.valueLogics = valueLogics;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
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

    public static class BusinessObjectVO {
        private String businessObject;
        private String valueLogic;

        public String getBusinessObject() {
            return businessObject;
        }

        public void setBusinessObject(String businessObject) {
            this.businessObject = businessObject;
        }

        public String getValueLogic() {
            return valueLogic;
        }

        public void setValueLogic(String valueLogic) {
            this.valueLogic = valueLogic;
        }
    }
}