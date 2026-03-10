package com.auth.letter;

import com.auth.letter.dto.AuthLetterDTO;
import com.auth.letter.dto.MatchRequest;
import com.auth.letter.dto.MatchResult;
import com.auth.letter.dto.SceneDTO;
import com.auth.letter.dto.RuleDTO;
import com.auth.letter.dto.ConditionDTO;
import com.auth.letter.enums.AuthLetterStatus;
import com.auth.letter.enums.OrgLevel;
import com.auth.letter.service.AuthLetterService;
import com.auth.letter.service.SceneService;
import com.auth.letter.service.RuleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class AuthLetterServiceTest {

    @Autowired
    private AuthLetterService authLetterService;

    @Autowired
    private SceneService sceneService;

    @Autowired
    private RuleService ruleService;

    private AuthLetterDTO testAuthLetter;

    @BeforeEach
    void setUp() {
        // Create a test authorization letter
        AuthLetterDTO dto = new AuthLetterDTO();
        dto.setCode("TEST-" + System.currentTimeMillis());
        dto.setName("Test Authorization Letter");
        dto.setDescription("Test Description");
        testAuthLetter = authLetterService.createAuthLetter(dto);
    }

    @Test
    void testCreateAuthLetter() {
        assertNotNull(testAuthLetter.getId());
        assertEquals("Test Authorization Letter", testAuthLetter.getName());
        assertEquals(AuthLetterStatus.DRAFT, testAuthLetter.getStatus());
    }

    @Test
    void testCreateAuthLetterWithDuplicateCode() {
        AuthLetterDTO dto = new AuthLetterDTO();
        dto.setCode(testAuthLetter.getCode());
        dto.setName("Another Letter");

        assertThrows(Exception.class, () -> authLetterService.createAuthLetter(dto));
    }

    @Test
    void testUpdateAuthLetter() {
        AuthLetterDTO updateDto = new AuthLetterDTO();
        updateDto.setCode(testAuthLetter.getCode());
        updateDto.setName("Updated Name");
        updateDto.setDescription("Updated Description");

        AuthLetterDTO updated = authLetterService.updateAuthLetter(testAuthLetter.getId(), updateDto);
        assertEquals("Updated Name", updated.getName());
        assertEquals("Updated Description", updated.getDescription());
    }

    @Test
    void testDeleteAuthLetter() {
        AuthLetterDTO dto = new AuthLetterDTO();
        dto.setCode("DELETE-TEST-" + System.currentTimeMillis());
        dto.setName("To Be Deleted");
        AuthLetterDTO created = authLetterService.createAuthLetter(dto);

        authLetterService.deleteAuthLetter(created.getId());

        assertThrows(Exception.class, () -> authLetterService.getAuthLetter(created.getId()));
    }

    @Test
    void testPublishAuthLetter() {
        // Create a scene first
        SceneDTO sceneDto = new SceneDTO();
        sceneDto.setName("Test Scene");
        sceneDto.setDecisionLevel(3);
        sceneService.createScene(testAuthLetter.getId(), sceneDto);

        AuthLetterDTO published = authLetterService.publishAuthLetter(
                testAuthLetter.getId(),
                OrgLevel.ORGANIZATION,
                OrgLevel.REPRESENTATIVE_OFFICE,
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(30)
        );

        assertEquals(AuthLetterStatus.PUBLISHED, published.getStatus());
        assertEquals(OrgLevel.ORGANIZATION, published.getPublishLevel());
        assertEquals(OrgLevel.REPRESENTATIVE_OFFICE, published.getAuthorizedLevel());
        assertNotNull(published.getPublishedAt());
    }

    @Test
    void testCannotUpdatePublishedAuthLetter() {
        // Create a scene first
        SceneDTO sceneDto = new SceneDTO();
        sceneDto.setName("Test Scene");
        sceneService.createScene(testAuthLetter.getId(), sceneDto);

        // Publish
        authLetterService.publishAuthLetter(
                testAuthLetter.getId(),
                OrgLevel.ORGANIZATION,
                OrgLevel.REPRESENTATIVE_OFFICE,
                null,
                null
        );

        // Try to update
        AuthLetterDTO updateDto = new AuthLetterDTO();
        updateDto.setCode(testAuthLetter.getCode());
        updateDto.setName("Updated Name");

        assertThrows(Exception.class, () -> authLetterService.updateAuthLetter(testAuthLetter.getId(), updateDto));
    }

    @Test
    void testCannotDeletePublishedAuthLetter() {
        // Create a scene first
        SceneDTO sceneDto = new SceneDTO();
        sceneDto.setName("Test Scene");
        sceneService.createScene(testAuthLetter.getId(), sceneDto);

        // Publish
        authLetterService.publishAuthLetter(
                testAuthLetter.getId(),
                OrgLevel.ORGANIZATION,
                OrgLevel.REPRESENTATIVE_OFFICE,
                null,
                null
        );

        // Try to delete
        assertThrows(Exception.class, () -> authLetterService.deleteAuthLetter(testAuthLetter.getId()));
    }
}