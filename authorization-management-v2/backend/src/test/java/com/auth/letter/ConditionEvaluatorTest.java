package com.auth.letter;

import com.auth.letter.dto.*;
import com.auth.letter.enums.LogicOperator;
import com.auth.letter.service.AuthLetterService;
import com.auth.letter.service.SceneService;
import com.auth.letter.service.RuleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class ConditionEvaluatorTest {

    @Autowired
    private AuthLetterService authLetterService;

    @Autowired
    private SceneService sceneService;

    @Autowired
    private RuleService ruleService;

    @BeforeEach
    void cleanUp() {
        // Clean up is handled by H2 in-memory database
    }

    @Test
    void testMatchSimpleCondition() {
        // Create auth letter
        AuthLetterDTO authDto = new AuthLetterDTO();
        authDto.setCode("MATCH-TEST-1-" + System.currentTimeMillis());
        authDto.setName("Match Test 1");
        AuthLetterDTO authLetter = authLetterService.createAuthLetter(authDto);

        // Create scene
        SceneDTO sceneDto = new SceneDTO();
        sceneDto.setName("Test Scene");
        sceneDto.setDecisionLevel(3);
        SceneDTO scene = sceneService.createScene(authLetter.getId(), sceneDto);

        // Create rule with simple condition
        RuleDTO ruleDto = new RuleDTO();
        ruleDto.setName("Amount Rule");

        List<ConditionDTO> conditions = new ArrayList<>();
        ConditionDTO condition = new ConditionDTO();
        condition.setFieldName("amount");
        condition.setOperator("GT");
        condition.setValue("10000");
        conditions.add(condition);
        ruleDto.setConditions(conditions);

        ruleService.createRule(scene.getId(), ruleDto);

        // Publish
        authLetterService.publishAuthLetter(authLetter.getId(), null, null, null, null);

        // Test match
        MatchRequest request = new MatchRequest();
        request.setAuthLetterCode(authLetter.getCode());
        Map<String, Object> context = new HashMap<>();
        context.put("amount", 15000);
        request.setContext(context);

        List<MatchResult> results = authLetterService.match(request);
        assertEquals(1, results.size());
        assertEquals(3, results.get(0).getDecisionLevel());
    }

    @Test
    void testMatchMultipleConditionsWithAnd() {
        // Create auth letter
        AuthLetterDTO authDto = new AuthLetterDTO();
        authDto.setCode("MATCH-TEST-2-" + System.currentTimeMillis());
        authDto.setName("Match Test 2");
        AuthLetterDTO authLetter = authLetterService.createAuthLetter(authDto);

        // Create scene
        SceneDTO sceneDto = new SceneDTO();
        sceneDto.setName("Test Scene");
        sceneDto.setDecisionLevel(2);
        SceneDTO scene = sceneService.createScene(authLetter.getId(), sceneDto);

        // Create rule with multiple conditions (AND)
        RuleDTO ruleDto = new RuleDTO();
        ruleDto.setName("Amount and Region Rule");

        List<ConditionDTO> conditions = new ArrayList<>();

        ConditionDTO condition1 = new ConditionDTO();
        condition1.setFieldName("amount");
        condition1.setOperator("GT");
        condition1.setValue("5000");
        condition1.setLogicOperator(LogicOperator.AND);
        conditions.add(condition1);

        ConditionDTO condition2 = new ConditionDTO();
        condition2.setFieldName("region");
        condition2.setOperator("EQ");
        condition2.setValue("east");
        conditions.add(condition2);

        ruleDto.setConditions(conditions);
        ruleService.createRule(scene.getId(), ruleDto);

        // Publish
        authLetterService.publishAuthLetter(authLetter.getId(), null, null, null, null);

        // Test match - should match
        MatchRequest request1 = new MatchRequest();
        request1.setAuthLetterCode(authLetter.getCode());
        Map<String, Object> context1 = new HashMap<>();
        context1.put("amount", 10000);
        context1.put("region", "east");
        request1.setContext(context1);

        List<MatchResult> results1 = authLetterService.match(request1);
        assertEquals(1, results1.size());

        // Test match - should not match (region is different)
        MatchRequest request2 = new MatchRequest();
        request2.setAuthLetterCode(authLetter.getCode());
        Map<String, Object> context2 = new HashMap<>();
        context2.put("amount", 10000);
        context2.put("region", "west");
        request2.setContext(context2);

        List<MatchResult> results2 = authLetterService.match(request2);
        assertEquals(0, results2.size());
    }

    @Test
    void testMatchNestedConditions() {
        // Create auth letter
        AuthLetterDTO authDto = new AuthLetterDTO();
        authDto.setCode("MATCH-TEST-3-" + System.currentTimeMillis());
        authDto.setName("Match Test 3");
        AuthLetterDTO authLetter = authLetterService.createAuthLetter(authDto);

        // Create scene
        SceneDTO sceneDto = new SceneDTO();
        sceneDto.setName("Test Scene");
        sceneDto.setDecisionLevel(1);
        SceneDTO scene = sceneService.createScene(authLetter.getId(), sceneDto);

        // Create rule with nested conditions: amount > 10000 AND (region = east OR region = west)
        RuleDTO ruleDto = new RuleDTO();
        ruleDto.setName("Complex Rule");

        List<ConditionDTO> conditions = new ArrayList<>();

        ConditionDTO condition1 = new ConditionDTO();
        condition1.setFieldName("amount");
        condition1.setOperator("GT");
        condition1.setValue("10000");
        condition1.setLogicOperator(LogicOperator.AND);
        conditions.add(condition1);

        // Nested group
        ConditionDTO nestedGroup = new ConditionDTO();
        nestedGroup.setLogicOperator(LogicOperator.OR);

        List<ConditionDTO> nestedConditions = new ArrayList<>();
        ConditionDTO nested1 = new ConditionDTO();
        nested1.setFieldName("region");
        nested1.setOperator("EQ");
        nested1.setValue("east");
        nested1.setLogicOperator(LogicOperator.OR);
        nestedConditions.add(nested1);

        ConditionDTO nested2 = new ConditionDTO();
        nested2.setFieldName("region");
        nested2.setOperator("EQ");
        nested2.setValue("west");
        nestedConditions.add(nested2);

        nestedGroup.setChildren(nestedConditions);
        conditions.add(nestedGroup);

        ruleDto.setConditions(conditions);
        ruleService.createRule(scene.getId(), ruleDto);

        // Publish
        authLetterService.publishAuthLetter(authLetter.getId(), null, null, null, null);

        // Test match - should match (amount > 10000 AND region = east)
        MatchRequest request1 = new MatchRequest();
        request1.setAuthLetterCode(authLetter.getCode());
        Map<String, Object> context1 = new HashMap<>();
        context1.put("amount", 15000);
        context1.put("region", "east");
        request1.setContext(context1);

        List<MatchResult> results1 = authLetterService.match(request1);
        assertEquals(1, results1.size());

        // Test match - should match (amount > 10000 AND region = west)
        MatchRequest request2 = new MatchRequest();
        request2.setAuthLetterCode(authLetter.getCode());
        Map<String, Object> context2 = new HashMap<>();
        context2.put("amount", 20000);
        context2.put("region", "west");
        request2.setContext(context2);

        List<MatchResult> results2 = authLetterService.match(request2);
        assertEquals(1, results2.size());

        // Test match - should not match (amount < 10000)
        MatchRequest request3 = new MatchRequest();
        request3.setAuthLetterCode(authLetter.getCode());
        Map<String, Object> context3 = new HashMap<>();
        context3.put("amount", 5000);
        context3.put("region", "east");
        request3.setContext(context3);

        List<MatchResult> results3 = authLetterService.match(request3);
        assertEquals(0, results3.size());

        // Test match - should not match (region = north, not in nested OR)
        MatchRequest request4 = new MatchRequest();
        request4.setAuthLetterCode(authLetter.getCode());
        Map<String, Object> context4 = new HashMap<>();
        context4.put("amount", 20000);
        context4.put("region", "north");
        request4.setContext(context4);

        List<MatchResult> results4 = authLetterService.match(request4);
        assertEquals(0, results4.size());
    }

    @Test
    void testMatchInOperator() {
        // Create auth letter
        AuthLetterDTO authDto = new AuthLetterDTO();
        authDto.setCode("MATCH-TEST-4-" + System.currentTimeMillis());
        authDto.setName("Match Test 4");
        AuthLetterDTO authLetter = authLetterService.createAuthLetter(authDto);

        // Create scene
        SceneDTO sceneDto = new SceneDTO();
        sceneDto.setName("Test Scene");
        sceneDto.setDecisionLevel(2);
        SceneDTO scene = sceneService.createScene(authLetter.getId(), sceneDto);

        // Create rule with IN condition
        RuleDTO ruleDto = new RuleDTO();
        ruleDto.setName("Region In Rule");

        List<ConditionDTO> conditions = new ArrayList<>();
        ConditionDTO condition = new ConditionDTO();
        condition.setFieldName("region");
        condition.setOperator("IN");
        condition.setValue("[\"east\", \"west\", \"south\"]");
        conditions.add(condition);
        ruleDto.setConditions(conditions);

        ruleService.createRule(scene.getId(), ruleDto);

        // Publish
        authLetterService.publishAuthLetter(authLetter.getId(), null, null, null, null);

        // Test match - should match (region = east)
        MatchRequest request1 = new MatchRequest();
        request1.setAuthLetterCode(authLetter.getCode());
        Map<String, Object> context1 = new HashMap<>();
        context1.put("region", "east");
        request1.setContext(context1);

        List<MatchResult> results1 = authLetterService.match(request1);
        assertEquals(1, results1.size());

        // Test match - should not match (region = north)
        MatchRequest request2 = new MatchRequest();
        request2.setAuthLetterCode(authLetter.getCode());
        Map<String, Object> context2 = new HashMap<>();
        context2.put("region", "north");
        request2.setContext(context2);

        List<MatchResult> results2 = authLetterService.match(request2);
        assertEquals(0, results2.size());
    }

    @Test
    void testMatchMultipleScenes() {
        // Create auth letter
        AuthLetterDTO authDto = new AuthLetterDTO();
        authDto.setCode("MATCH-TEST-5-" + System.currentTimeMillis());
        authDto.setName("Match Test 5");
        AuthLetterDTO authLetter = authLetterService.createAuthLetter(authDto);

        // Create scene 1
        SceneDTO scene1Dto = new SceneDTO();
        scene1Dto.setName("Scene 1");
        scene1Dto.setDecisionLevel(1);
        SceneDTO scene1 = sceneService.createScene(authLetter.getId(), scene1Dto);

        RuleDTO rule1Dto = new RuleDTO();
        rule1Dto.setName("Rule 1");
        List<ConditionDTO> conditions1 = new ArrayList<>();
        ConditionDTO condition1 = new ConditionDTO();
        condition1.setFieldName("type");
        condition1.setOperator("EQ");
        condition1.setValue("A");
        conditions1.add(condition1);
        rule1Dto.setConditions(conditions1);
        ruleService.createRule(scene1.getId(), rule1Dto);

        // Create scene 2
        SceneDTO scene2Dto = new SceneDTO();
        scene2Dto.setName("Scene 2");
        scene2Dto.setDecisionLevel(2);
        SceneDTO scene2 = sceneService.createScene(authLetter.getId(), scene2Dto);

        RuleDTO rule2Dto = new RuleDTO();
        rule2Dto.setName("Rule 2");
        List<ConditionDTO> conditions2 = new ArrayList<>();
        ConditionDTO condition2 = new ConditionDTO();
        condition2.setFieldName("type");
        condition2.setOperator("EQ");
        condition2.setValue("B");
        conditions2.add(condition2);
        rule2Dto.setConditions(conditions2);
        ruleService.createRule(scene2.getId(), rule2Dto);

        // Publish
        authLetterService.publishAuthLetter(authLetter.getId(), null, null, null, null);

        // Test match - both scenes should match
        MatchRequest request = new MatchRequest();
        request.setAuthLetterCode(authLetter.getCode());
        Map<String, Object> context = new HashMap<>();
        context.put("type", "A");
        request.setContext(context);

        List<MatchResult> results = authLetterService.match(request);
        // Only scene 1 should match (type = A)
        assertEquals(1, results.size());
        assertEquals(1, results.get(0).getDecisionLevel());
    }
}