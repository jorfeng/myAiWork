package com.auth.letter.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.auth.letter.dao.AuthLetterDao;
import com.auth.letter.dao.AuthLookupValueDao;
import com.auth.letter.entity.AuthLetter;
import com.auth.letter.entity.AuthLookupValue;
import com.auth.letter.service.AuthLetterService;
import com.auth.letter.service.OperationLogService;
import com.auth.letter.vo.AuthLetterCreateRequest;
import com.auth.letter.vo.AuthLetterQueryRequest;
import com.auth.letter.vo.AuthLetterVO;
import com.auth.letter.vo.PageResult;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class AuthLetterServiceImpl implements AuthLetterService {

    @Autowired
    private AuthLetterDao authLetterDao;

    @Autowired
    private AuthLookupValueDao lookupValueDao;

    @Autowired
    private OperationLogService operationLogService;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public PageResult<AuthLetterVO> queryList(AuthLetterQueryRequest request) {
        List<AuthLetter> list = authLetterDao.selectList(
            request.getName(),
            listToJson(request.getAuthTargetLevel()),
            listToJson(request.getApplicableRegion()),
            listToJson(request.getAuthPublishLevel()),
            listToJson(request.getAuthPublishOrg()),
            request.getPublishYear(),
            request.getStatus(),
            request.getOffset(),
            request.getPageSize()
        );

        Long total = authLetterDao.countList(
            request.getName(),
            listToJson(request.getAuthTargetLevel()),
            listToJson(request.getApplicableRegion()),
            listToJson(request.getAuthPublishLevel()),
            listToJson(request.getAuthPublishOrg()),
            request.getPublishYear(),
            request.getStatus()
        );

        List<AuthLetterVO> voList = list.stream()
            .map(this::convertToVO)
            .collect(Collectors.toList());

        return new PageResult<>(voList, total, request.getPageNum(), request.getPageSize());
    }

    @Override
    public AuthLetterVO getDetail(Long id) {
        AuthLetter letter = authLetterDao.selectById(id);
        if (letter == null) {
            return null;
        }
        return convertToVO(letter);
    }

    @Override
    @Transactional
    public Long create(AuthLetterCreateRequest request) {
        AuthLetter letter = new AuthLetter();
        letter.setName(request.getName());
        letter.setAuthTargetLevel(listToJson(request.getAuthTargetLevel()));
        letter.setApplicableRegion(listToJson(request.getApplicableRegion()));
        letter.setAuthPublishLevel(listToJson(request.getAuthPublishLevel()));
        letter.setAuthPublishOrg(listToJson(request.getAuthPublishOrg()));
        letter.setPublishYear(request.getPublishYear());
        letter.setSummary(request.getSummary());
        letter.setStatus("DRAFT");
        letter.setCreatedBy("system");
        letter.setCreatedTime(LocalDateTime.now());
        letter.setUpdatedBy("system");
        letter.setUpdatedTime(LocalDateTime.now());

        authLetterDao.insert(letter);
        operationLogService.log(letter.getId(), "创建", "system");

        return letter.getId();
    }

    @Override
    @Transactional
    public void update(AuthLetterCreateRequest request) {
        AuthLetter letter = authLetterDao.selectById(request.getId());
        if (letter == null) {
            throw new RuntimeException("授权书不存在");
        }

        if (!"DRAFT".equals(letter.getStatus())) {
            throw new RuntimeException("只有草稿状态的授权书才能修改");
        }

        letter.setName(request.getName());
        letter.setAuthTargetLevel(listToJson(request.getAuthTargetLevel()));
        letter.setApplicableRegion(listToJson(request.getApplicableRegion()));
        letter.setAuthPublishLevel(listToJson(request.getAuthPublishLevel()));
        letter.setAuthPublishOrg(listToJson(request.getAuthPublishOrg()));
        letter.setPublishYear(request.getPublishYear());
        letter.setSummary(request.getSummary());
        letter.setUpdatedBy("system");
        letter.setUpdatedTime(LocalDateTime.now());

        authLetterDao.update(letter);
        operationLogService.log(letter.getId(), "更新", "system");
    }

    @Override
    @Transactional
    public void delete(Long id) {
        AuthLetter letter = authLetterDao.selectById(id);
        if (letter == null) {
            throw new RuntimeException("授权书不存在");
        }

        authLetterDao.deleteById(id);
        operationLogService.log(id, "删除", "system");
    }

    @Override
    @Transactional
    public void publish(Long id) {
        AuthLetter letter = authLetterDao.selectById(id);
        if (letter == null) {
            throw new RuntimeException("授权书不存在");
        }

        if (!"DRAFT".equals(letter.getStatus())) {
            throw new RuntimeException("只有草稿状态的授权书才能发布");
        }

        authLetterDao.updateStatus(id, "PUBLISHED");
        operationLogService.log(id, "发布", "system");
    }

    @Override
    @Transactional
    public void activate(Long id) {
        AuthLetter letter = authLetterDao.selectById(id);
        if (letter == null) {
            throw new RuntimeException("授权书不存在");
        }

        authLetterDao.updateStatus(id, "PUBLISHED");
        operationLogService.log(id, "生效", "system");
    }

    @Override
    @Transactional
    public void deactivate(Long id) {
        AuthLetter letter = authLetterDao.selectById(id);
        if (letter == null) {
            throw new RuntimeException("授权书不存在");
        }

        authLetterDao.updateStatus(id, "INVALID");
        operationLogService.log(id, "失效", "system");
    }

    @Override
    @Transactional
    public void deleteBatch(List<Long> ids) {
        for (Long id : ids) {
            delete(id);
        }
    }

    private AuthLetterVO convertToVO(AuthLetter letter) {
        AuthLetterVO vo = new AuthLetterVO();
        vo.setId(letter.getId());
        vo.setName(letter.getName());
        vo.setAuthTargetLevel(jsonToList(letter.getAuthTargetLevel()));
        vo.setApplicableRegion(jsonToList(letter.getApplicableRegion()));
        vo.setAuthPublishLevel(jsonToList(letter.getAuthPublishLevel()));
        vo.setAuthPublishOrg(jsonToList(letter.getAuthPublishOrg()));
        vo.setPublishYear(letter.getPublishYear());
        vo.setSummary(letter.getSummary());
        vo.setStatus(letter.getStatus());
        vo.setCreatedBy(letter.getCreatedBy());
        vo.setCreatedTime(letter.getCreatedTime());
        vo.setUpdatedBy(letter.getUpdatedBy());
        vo.setUpdatedTime(letter.getUpdatedTime());

        vo.setAuthTargetLevelLabel(getLabels("AUTH_TARGET_LEVEL", vo.getAuthTargetLevel()));
        vo.setApplicableRegionLabel(getLabels("APPLICABLE_REGION", vo.getApplicableRegion()));
        vo.setAuthPublishLevelLabel(getLabels("AUTH_PUBLISH_LEVEL", vo.getAuthPublishLevel()));
        vo.setAuthPublishOrgLabel(getLabels("AUTH_PUBLISH_ORG", vo.getAuthPublishOrg()));

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

    private String getLabels(String typeCode, List<String> values) {
        if (values == null || values.isEmpty()) {
            return "";
        }
        List<AuthLookupValue> lookupValues = lookupValueDao.selectByTypeCodeAndValues(typeCode, values);
        return lookupValues.stream()
            .map(AuthLookupValue::getLabel)
            .collect(Collectors.joining(","));
    }
}