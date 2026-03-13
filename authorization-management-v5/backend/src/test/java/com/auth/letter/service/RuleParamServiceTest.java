package com.auth.letter.service;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.auth.letter.vo.AuthRuleParamVO;
import com.auth.letter.vo.PageResult;
import com.auth.letter.vo.RuleParamCreateRequest;
import com.auth.letter.vo.RuleParamQueryRequest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class RuleParamServiceTest {

    @Autowired
    private RuleParamService ruleParamService;

    private Long testParamId;

    @BeforeEach
    public void setUp() {
        RuleParamCreateRequest request = new RuleParamCreateRequest();
        request.setName("测试规则参数");
        request.setNameEn("testRuleParam");
        request.setIsActive(true);
        request.setDataType("TEXT");
        testParamId = ruleParamService.create(request);
    }

    @Test
    public void testCreate() {
        assertNotNull(testParamId);
        assertTrue(testParamId > 0);
    }

    @Test
    public void testQueryList() {
        RuleParamQueryRequest request = new RuleParamQueryRequest();
        request.setPageNum(1);
        request.setPageSize(10);

        PageResult<AuthRuleParamVO> result = ruleParamService.queryList(request);
        assertNotNull(result);
        assertTrue(result.getTotal() > 0);
    }

    @Test
    public void testUpdate() {
        RuleParamCreateRequest request = new RuleParamCreateRequest();
        request.setId(testParamId);
        request.setName("更新后的规则参数");
        request.setNameEn("updatedRuleParam");
        request.setIsActive(true);
        request.setDataType("NUMBER");

        ruleParamService.update(request);

        PageResult<AuthRuleParamVO> result = ruleParamService.queryList(new RuleParamQueryRequest());
        AuthRuleParamVO vo = result.getList().stream()
            .filter(v -> v.getId().equals(testParamId))
            .findFirst()
            .orElse(null);
        assertNotNull(vo);
        assertEquals("更新后的规则参数", vo.getName());
    }

    @Test
    public void testActivateAndDeactivate() {
        ruleParamService.deactivate(testParamId);

        PageResult<AuthRuleParamVO> result = ruleParamService.queryList(new RuleParamQueryRequest());
        AuthRuleParamVO vo = result.getList().stream()
            .filter(v -> v.getId().equals(testParamId))
            .findFirst()
            .orElse(null);
        assertNotNull(vo);
        assertEquals("INACTIVE", vo.getStatus());

        ruleParamService.activate(testParamId);

        result = ruleParamService.queryList(new RuleParamQueryRequest());
        vo = result.getList().stream()
            .filter(v -> v.getId().equals(testParamId))
            .findFirst()
            .orElse(null);
        assertNotNull(vo);
        assertEquals("ACTIVE", vo.getStatus());
    }

    @Test
    public void testGetAllActive() {
        List<AuthRuleParamVO> list = ruleParamService.getAllActive();
        assertNotNull(list);
    }
}