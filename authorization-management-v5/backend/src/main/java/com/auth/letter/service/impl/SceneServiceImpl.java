package com.auth.letter.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.auth.letter.dao.AuthSceneDao;
import com.auth.letter.entity.AuthScene;
import com.auth.letter.service.SceneService;
import com.auth.letter.vo.AuthSceneVO;
import com.auth.letter.vo.PageResult;
import com.auth.letter.vo.SceneCreateRequest;
import com.auth.letter.vo.SceneMatchResult;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class SceneServiceImpl implements SceneService {

    @Autowired
    private AuthSceneDao sceneDao;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public PageResult<AuthSceneVO> queryByLetterId(Long letterId, Integer pageNum, Integer pageSize) {
        Integer offset = (pageNum - 1) * pageSize;
        List<AuthScene> list = sceneDao.selectByLetterId(letterId, offset, pageSize);
        Long total = sceneDao.countByLetterId(letterId);

        List<AuthSceneVO> voList = list.stream()
            .map(this::convertToVO)
            .collect(Collectors.toList());

        return new PageResult<>(voList, total, pageNum, pageSize);
    }

    @Override
    @Transactional
    public Long create(SceneCreateRequest request) {
        AuthScene scene = new AuthScene();
        scene.setLetterId(request.getLetterId());
        scene.setSceneName(request.getSceneName());
        scene.setIndustry(listToJson(request.getIndustry()));
        scene.setBusinessScene(request.getBusinessScene());
        scene.setDecisionLevel(request.getDecisionLevel());
        scene.setSpecificRule(request.getSpecificRule());
        scene.setRuleConfig(objectToJson(request.getRuleConfig()));
        scene.setCreatedBy("system");
        scene.setCreatedTime(LocalDateTime.now());
        scene.setUpdatedBy("system");
        scene.setUpdatedTime(LocalDateTime.now());

        sceneDao.insert(scene);
        return scene.getId();
    }

    @Override
    @Transactional
    public void update(SceneCreateRequest request) {
        AuthScene scene = sceneDao.selectById(request.getId());
        if (scene == null) {
            throw new RuntimeException("场景不存在");
        }

        scene.setSceneName(request.getSceneName());
        scene.setIndustry(listToJson(request.getIndustry()));
        scene.setBusinessScene(request.getBusinessScene());
        scene.setDecisionLevel(request.getDecisionLevel());
        scene.setSpecificRule(request.getSpecificRule());
        scene.setRuleConfig(objectToJson(request.getRuleConfig()));
        scene.setUpdatedBy("system");
        scene.setUpdatedTime(LocalDateTime.now());

        sceneDao.update(scene);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        sceneDao.deleteById(id);
    }

    @Override
    @Transactional
    public void deleteBatch(List<Long> ids) {
        if (ids != null && !ids.isEmpty()) {
            sceneDao.deleteByIds(ids);
        }
    }

    @Override
    public List<SceneMatchResult> matchScenes(Long letterId, Map<String, Object> params) {
        List<AuthScene> scenes = sceneDao.selectByLetterIdForMatch(letterId);
        List<SceneMatchResult> results = new ArrayList<>();

        for (AuthScene scene : scenes) {
            if (isSceneMatch(scene, params)) {
                results.add(new SceneMatchResult(scene.getId(), scene.getSceneName(), scene.getDecisionLevel()));
            }
        }

        return results;
    }

    private boolean isSceneMatch(AuthScene scene, Map<String, Object> params) {
        String ruleConfig = scene.getRuleConfig();
        if (ruleConfig == null || ruleConfig.isEmpty()) {
            return true;
        }

        try {
            Map<String, Object> config = objectMapper.readValue(ruleConfig, new TypeReference<Map<String, Object>>() {});
            return evaluateConditionGroup(config, params);
        } catch (JsonProcessingException e) {
            return true;
        }
    }

    @SuppressWarnings("unchecked")
    private boolean evaluateConditionGroup(Map<String, Object> group, Map<String, Object> params) {
        String logic = (String) group.get("logic");
        List<Map<String, Object>> conditions = (List<Map<String, Object>>) group.get("conditions");

        if (conditions == null || conditions.isEmpty()) {
            return true;
        }

        boolean result = "AND".equals(logic);

        for (Map<String, Object> condition : conditions) {
            String type = (String) condition.get("type");
            boolean conditionResult;

            if ("group".equals(type)) {
                conditionResult = evaluateConditionGroup(condition, params);
            } else {
                conditionResult = evaluateCondition(condition, params);
            }

            if ("AND".equals(logic)) {
                result = result && conditionResult;
            } else {
                result = result || conditionResult;
            }
        }

        return result;
    }

    private boolean evaluateCondition(Map<String, Object> condition, Map<String, Object> params) {
        String fieldName = (String) condition.get("fieldName");
        String operator = (String) condition.get("operator");
        String compareType = (String) condition.get("compareType");
        Object compareValue = condition.get("compareValue");

        Object paramValue = params.get(fieldName);
        if (paramValue == null) {
            return false;
        }

        if ("NUMBER".equals(compareType)) {
            double paramNum = toNumber(paramValue);
            double compareNum = toNumber(compareValue);

            switch (operator) {
                case ">": return paramNum > compareNum;
                case "<": return paramNum < compareNum;
                case "=": return paramNum == compareNum;
                case ">=": return paramNum >= compareNum;
                case "<=": return paramNum <= compareNum;
                case "!=": return paramNum != compareNum;
                default: return false;
            }
        } else {
            String paramStr = String.valueOf(paramValue);
            String compareStr = String.valueOf(compareValue);

            switch (operator) {
                case "=": return paramStr.equals(compareStr);
                case "!=": return !paramStr.equals(compareStr);
                default: return false;
            }
        }
    }

    private double toNumber(Object value) {
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        try {
            return Double.parseDouble(String.valueOf(value));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private AuthSceneVO convertToVO(AuthScene scene) {
        AuthSceneVO vo = new AuthSceneVO();
        vo.setId(scene.getId());
        vo.setLetterId(scene.getLetterId());
        vo.setSceneName(scene.getSceneName());
        vo.setIndustry(jsonToList(scene.getIndustry()));
        vo.setBusinessScene(scene.getBusinessScene());
        vo.setDecisionLevel(scene.getDecisionLevel());
        vo.setSpecificRule(scene.getSpecificRule());
        vo.setRuleConfig(jsonToObject(scene.getRuleConfig()));
        vo.setCreatedBy(scene.getCreatedBy());
        vo.setCreatedTime(scene.getCreatedTime());
        vo.setUpdatedBy(scene.getUpdatedBy());
        vo.setUpdatedTime(scene.getUpdatedTime());
        return vo;
    }

    private String listToJson(List<String> list) {
        if (list == null || list.isEmpty()) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(list);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    private List<String> jsonToList(String json) {
        if (json == null || json.isEmpty()) {
            return new ArrayList<>();
        }
        try {
            return objectMapper.readValue(json, new TypeReference<List<String>>() {});
        } catch (JsonProcessingException e) {
            return new ArrayList<>();
        }
    }

    private String objectToJson(Object obj) {
        if (obj == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    private Object jsonToObject(String json) {
        if (json == null || json.isEmpty()) {
            return null;
        }
        try {
            return objectMapper.readValue(json, Object.class);
        } catch (JsonProcessingException e) {
            return null;
        }
    }
}