package com.auth.letter.vo;

import java.util.List;

public class SceneCreateRequest {
    private Long id;
    private Long letterId;
    private String sceneName;
    private List<String> industry;
    private String businessScene;
    private String decisionLevel;
    private String specificRule;
    private Object ruleConfig;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getLetterId() {
        return letterId;
    }

    public void setLetterId(Long letterId) {
        this.letterId = letterId;
    }

    public String getSceneName() {
        return sceneName;
    }

    public void setSceneName(String sceneName) {
        this.sceneName = sceneName;
    }

    public List<String> getIndustry() {
        return industry;
    }

    public void setIndustry(List<String> industry) {
        this.industry = industry;
    }

    public String getBusinessScene() {
        return businessScene;
    }

    public void setBusinessScene(String businessScene) {
        this.businessScene = businessScene;
    }

    public String getDecisionLevel() {
        return decisionLevel;
    }

    public void setDecisionLevel(String decisionLevel) {
        this.decisionLevel = decisionLevel;
    }

    public String getSpecificRule() {
        return specificRule;
    }

    public void setSpecificRule(String specificRule) {
        this.specificRule = specificRule;
    }

    public Object getRuleConfig() {
        return ruleConfig;
    }

    public void setRuleConfig(Object ruleConfig) {
        this.ruleConfig = ruleConfig;
    }
}