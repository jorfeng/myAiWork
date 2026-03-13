package com.auth.letter.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.auth.letter.dao.AuthRuleParamDao;
import com.auth.letter.entity.AuthRuleParam;
import com.auth.letter.service.RuleParamService;
import com.auth.letter.vo.AuthRuleParamVO;
import com.auth.letter.vo.PageResult;
import com.auth.letter.vo.RuleParamCreateRequest;
import com.auth.letter.vo.RuleParamQueryRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class RuleParamServiceImpl implements RuleParamService {

    @Autowired
    private AuthRuleParamDao ruleParamDao;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public PageResult<AuthRuleParamVO> queryList(RuleParamQueryRequest request) {
        List<AuthRuleParam> list = ruleParamDao.selectList(
            request.getName(),
            request.getNameEn(),
            request.getStatus(),
            request.getOffset(),
            request.getPageSize()
        );

        Long total = ruleParamDao.countList(
            request.getName(),
            request.getNameEn(),
            request.getStatus()
        );

        List<AuthRuleParamVO> voList = list.stream()
            .map(this::convertToVO)
            .collect(Collectors.toList());

        return new PageResult<>(voList, total, request.getPageNum(), request.getPageSize());
    }

    @Override
    @Transactional
    public Long create(RuleParamCreateRequest request) {
        AuthRuleParam ruleParam = new AuthRuleParam();
        ruleParam.setName(request.getName());
        ruleParam.setNameEn(request.getNameEn());
        ruleParam.setBusinessObjects(objectToJson(request.getBusinessObjects()));
        ruleParam.setValueLogics(listToJson(request.getValueLogics()));
        ruleParam.setIsActive(request.getIsActive());
        ruleParam.setDataType(request.getDataType());
        ruleParam.setStatus("ACTIVE");
        ruleParam.setCreatedBy("system");
        ruleParam.setCreatedTime(LocalDateTime.now());
        ruleParam.setUpdatedBy("system");
        ruleParam.setUpdatedTime(LocalDateTime.now());

        ruleParamDao.insert(ruleParam);
        return ruleParam.getId();
    }

    @Override
    @Transactional
    public void update(RuleParamCreateRequest request) {
        AuthRuleParam ruleParam = ruleParamDao.selectById(request.getId());
        if (ruleParam == null) {
            throw new RuntimeException("规则参数不存在");
        }

        ruleParam.setName(request.getName());
        ruleParam.setNameEn(request.getNameEn());
        ruleParam.setBusinessObjects(objectToJson(request.getBusinessObjects()));
        ruleParam.setValueLogics(listToJson(request.getValueLogics()));
        ruleParam.setIsActive(request.getIsActive());
        ruleParam.setDataType(request.getDataType());
        ruleParam.setUpdatedBy("system");
        ruleParam.setUpdatedTime(LocalDateTime.now());

        ruleParamDao.update(ruleParam);
    }

    @Override
    @Transactional
    public void activate(Long id) {
        ruleParamDao.updateStatus(id, "ACTIVE");
    }

    @Override
    @Transactional
    public void deactivate(Long id) {
        ruleParamDao.updateStatus(id, "INACTIVE");
    }

    @Override
    @Transactional
    public void activateBatch(List<Long> ids) {
        if (ids != null && !ids.isEmpty()) {
            ruleParamDao.updateStatusByIds(ids, "ACTIVE");
        }
    }

    @Override
    @Transactional
    public void deactivateBatch(List<Long> ids) {
        if (ids != null && !ids.isEmpty()) {
            ruleParamDao.updateStatusByIds(ids, "INACTIVE");
        }
    }

    @Override
    public List<AuthRuleParamVO> getAllActive() {
        List<AuthRuleParam> list = ruleParamDao.selectAllActive();
        return list.stream()
            .map(this::convertToVO)
            .collect(Collectors.toList());
    }

    private AuthRuleParamVO convertToVO(AuthRuleParam ruleParam) {
        AuthRuleParamVO vo = new AuthRuleParamVO();
        vo.setId(ruleParam.getId());
        vo.setName(ruleParam.getName());
        vo.setNameEn(ruleParam.getNameEn());
        vo.setBusinessObjects(jsonToBusinessObjects(ruleParam.getBusinessObjects()));
        vo.setValueLogics(jsonToList(ruleParam.getValueLogics()));
        vo.setIsActive(ruleParam.getIsActive());
        vo.setDataType(ruleParam.getDataType());
        vo.setStatus(ruleParam.getStatus());
        vo.setCreatedBy(ruleParam.getCreatedBy());
        vo.setCreatedTime(ruleParam.getCreatedTime());
        vo.setUpdatedBy(ruleParam.getUpdatedBy());
        vo.setUpdatedTime(ruleParam.getUpdatedTime());
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

    @SuppressWarnings("unchecked")
    private List<AuthRuleParamVO.BusinessObjectVO> jsonToBusinessObjects(String json) {
        if (json == null || json.isEmpty()) {
            return new ArrayList<>();
        }
        try {
            List<Map<String, String>> list = objectMapper.readValue(json,
                new TypeReference<List<Map<String, String>>>() {});
            return list.stream().map(map -> {
                AuthRuleParamVO.BusinessObjectVO vo = new AuthRuleParamVO.BusinessObjectVO();
                vo.setBusinessObject(map.get("businessObject"));
                vo.setValueLogic(map.get("valueLogic"));
                return vo;
            }).collect(Collectors.toList());
        } catch (JsonProcessingException e) {
            return new ArrayList<>();
        }
    }
}