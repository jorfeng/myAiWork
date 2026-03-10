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

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class RuleServiceTest {

    @Autowired
    private RuleService ruleService;

    @Autowired
    private SceneService sceneService;

    @Autowired
    private AuthLetterService authLetterService;

    private AuthLetterDTO testAuthLetter;
    private SceneDTO testScene;

    @BeforeEach
    void setUp() {
        AuthLetterDTO authDto = new AuthLetterDTO();
        authDto.setCode("RULE-TEST-" + System.currentTimeMillis());
        authDto.setName("Test Auth Letter for Rule");
        testAuthLetter = authLetterService.createAuthLetter(authDto);

        SceneDTO sceneDto = new SceneDTO();
        sceneDto.setName("Test Scene for Rule");
        sceneDto.setDecisionLevel(3);
        testScene = sceneService.createScene(testAuthLetter.getId(), sceneDto);
    }

    @Test
    void testCreateRule() {
        RuleDTO ruleDto = new RuleDTO();
        ruleDto.setName("Test Rule");
        ruleDto.setDescription("Test Description");
        ruleDto.setOrderIndex(0);

        RuleDTO created = ruleService.createRule(testScene.getId(), ruleDto);

        assertNotNull(created.getId());
        assertEquals("Test Rule", created.getName());
    }

    @Test
    void testCreateRuleWithConditions() {
        RuleDTO ruleDto = new RuleDTO();
        ruleDto.setName("Rule with Conditions");
        ruleDto.setOrderIndex(0);

        List<ConditionDTO> conditions = new ArrayList<>();
        ConditionDTO condition1 = new ConditionDTO();
        condition1.setFieldName("amount");
        condition1.setOperator("GT");
        condition1.setValue("10000");
        condition1.setLogicOperator(LogicOperator.AND);
        conditions.add(condition1);

        ConditionDTO condition2 = new ConditionDTO();
        condition2.setFieldName("region");
        condition2.setOperator("EQ");
        condition2.setValue("east");
        conditions.add(condition2);

        ruleDto.setConditions(conditions);

        RuleDTO created = ruleService.createRule(testScene.getId(), ruleDto);

        assertNotNull(created.getId());
        assertEquals(2, created.getConditions().size());
    }

    @Test
    void testCreateRuleWithNestedConditions() {
        RuleDTO ruleDto = new RuleDTO();
        ruleDto.setName("Rule with Nested Conditions");
        ruleDto.setOrderIndex(0);

        List<ConditionDTO> conditions = new ArrayList<>();

        // First condition: amount > 10000
        ConditionDTO condition1 = new ConditionDTO();
        condition1.setFieldName("amount");
        condition1.setOperator("GT");
        condition1.setValue("10000");
        condition1.setLogicOperator(LogicOperator.AND);
        conditions.add(condition1);

        // Nested condition group: (region = east OR region = west)
        ConditionDTO nestedGroup = new ConditionDTO();
        nestedGroup.setLogicOperator(LogicOperator.AND);

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

        RuleDTO created = ruleService.createRule(testScene.getId(), ruleDto);

        assertNotNull(created.getId());
        assertEquals(2, created.getConditions().size());
        assertTrue(created.getConditions().get(1).getChildren().size() > 0);
    }

    @Test
    void testUpdateRule() {
        RuleDTO ruleDto = new RuleDTO();
        ruleDto.setName("Original Name");
        RuleDTO created = ruleService.createRule(testScene.getId(), ruleDto);

        RuleDTO updateDto = new RuleDTO();
        updateDto.setName("Updated Name");
        RuleDTO updated = ruleService.updateRule(created.getId(), updateDto);

        assertEquals("Updated Name", updated.getName());
    }

    @Test
    void testDeleteRule() {
        RuleDTO ruleDto = new RuleDTO();
        ruleDto.setName("To Be Deleted");
        RuleDTO created = ruleService.createRule(testScene.getId(), ruleDto);

        ruleService.deleteRule(created.getId());

        assertThrows(Exception.class, () -> ruleService.getRule(created.getId()));
    }

    @Test
    void testGetRulesBySceneId() {
        RuleDTO rule1 = new RuleDTO();
        rule1.setName("Rule 1");
        ruleService.createRule(testScene.getId(), rule1);

        RuleDTO rule2 = new RuleDTO();
        rule2.setName("Rule 2");
        ruleService.createRule(testScene.getId(), rule2);

        List<RuleDTO> rules = ruleService.getRulesBySceneId(testScene.getId());
        assertEquals(2, rules.size());
    }
}