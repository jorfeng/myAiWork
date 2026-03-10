package com.auth.letter;

import com.auth.letter.dto.SceneDTO;
import com.auth.letter.dto.AuthLetterDTO;
import com.auth.letter.enums.AuthLetterStatus;
import com.auth.letter.service.SceneService;
import com.auth.letter.service.AuthLetterService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class SceneServiceTest {

    @Autowired
    private SceneService sceneService;

    @Autowired
    private AuthLetterService authLetterService;

    private AuthLetterDTO testAuthLetter;

    @BeforeEach
    void setUp() {
        AuthLetterDTO dto = new AuthLetterDTO();
        dto.setCode("SCENE-TEST-" + System.currentTimeMillis());
        dto.setName("Test Auth Letter for Scene");
        testAuthLetter = authLetterService.createAuthLetter(dto);
    }

    @Test
    void testCreateScene() {
        SceneDTO sceneDto = new SceneDTO();
        sceneDto.setName("Test Scene");
        sceneDto.setDescription("Test Description");
        sceneDto.setDecisionLevel(3);
        sceneDto.setOrderIndex(0);

        SceneDTO created = sceneService.createScene(testAuthLetter.getId(), sceneDto);

        assertNotNull(created.getId());
        assertEquals("Test Scene", created.getName());
        assertEquals(3, created.getDecisionLevel());
    }

    @Test
    void testUpdateScene() {
        // Create
        SceneDTO sceneDto = new SceneDTO();
        sceneDto.setName("Original Name");
        sceneDto.setDecisionLevel(2);
        SceneDTO created = sceneService.createScene(testAuthLetter.getId(), sceneDto);

        // Update
        SceneDTO updateDto = new SceneDTO();
        updateDto.setName("Updated Name");
        updateDto.setDecisionLevel(4);
        SceneDTO updated = sceneService.updateScene(created.getId(), updateDto);

        assertEquals("Updated Name", updated.getName());
        assertEquals(4, updated.getDecisionLevel());
    }

    @Test
    void testDeleteScene() {
        SceneDTO sceneDto = new SceneDTO();
        sceneDto.setName("To Be Deleted");
        SceneDTO created = sceneService.createScene(testAuthLetter.getId(), sceneDto);

        sceneService.deleteScene(created.getId());

        assertThrows(Exception.class, () -> sceneService.getScene(created.getId()));
    }

    @Test
    void testGetScenesByAuthLetterId() {
        SceneDTO scene1 = new SceneDTO();
        scene1.setName("Scene 1");
        scene1.setOrderIndex(1);
        sceneService.createScene(testAuthLetter.getId(), scene1);

        SceneDTO scene2 = new SceneDTO();
        scene2.setName("Scene 2");
        scene2.setOrderIndex(2);
        sceneService.createScene(testAuthLetter.getId(), scene2);

        List<SceneDTO> scenes = sceneService.getScenesByAuthLetterId(testAuthLetter.getId());
        assertEquals(2, scenes.size());
    }

    @Test
    void testCannotAddSceneToPublishedAuthLetter() {
        // Create and publish
        SceneDTO sceneDto = new SceneDTO();
        sceneDto.setName("Initial Scene");
        sceneService.createScene(testAuthLetter.getId(), sceneDto);

        authLetterService.publishAuthLetter(
                testAuthLetter.getId(),
                null,
                null,
                null,
                null
        );

        // Try to add another scene
        SceneDTO newScene = new SceneDTO();
        newScene.setName("New Scene");

        assertThrows(Exception.class, () -> sceneService.createScene(testAuthLetter.getId(), newScene));
    }
}