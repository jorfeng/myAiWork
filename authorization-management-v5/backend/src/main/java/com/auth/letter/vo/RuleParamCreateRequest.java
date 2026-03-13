package com.auth.letter.vo;

import java.util.List;

public class RuleParamCreateRequest {
    private Long id;
    private String name;
    private String nameEn;
    private List<AuthRuleParamVO.BusinessObjectVO> businessObjects;
    private List<String> valueLogics;
    private Boolean isActive;
    private String dataType;

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

    public List<AuthRuleParamVO.BusinessObjectVO> getBusinessObjects() {
        return businessObjects;
    }

    public void setBusinessObjects(List<AuthRuleParamVO.BusinessObjectVO> businessObjects) {
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
}