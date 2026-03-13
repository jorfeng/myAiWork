package com.auth.letter.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.auth.letter.vo.AuthLetterCreateRequest;
import com.auth.letter.vo.AuthSceneVO;
import com.auth.letter.vo.PageResult;
import com.auth.letter.vo.SceneCreateRequest;
import com.auth.letter.vo.SceneMatchResult;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class SceneServiceTest {

    @Autowired
    private SceneService sceneService;

    @Autowired
    private AuthLetterService authLetterService;

    private Long testLetterId;
    private Long testSceneId;

    @BeforeEach
    public void setUp() {
        AuthLetterCreateRequest letterRequest = new AuthLetterCreateRequest();
        letterRequest.setName("测试授权书");
        testLetterId = authLetterService.create(letterRequest);

        SceneCreateRequest sceneRequest = new SceneCreateRequest();
        sceneRequest.setLetterId(testLetterId);
        sceneRequest.setSceneName("测试场景");
        sceneRequest.setBusinessScene("SCENE_CONTRACT");
        sceneRequest.setDecisionLevel("DEC_BOARD");
        testSceneId = sceneService.create(sceneRequest);
    }

    @Test
    public void testCreate() {
        assertNotNull(testSceneId);
        assertTrue(testSceneId > 0);
    }

    @Test
    public void testQueryByLetterId() {
        PageResult<AuthSceneVO> result = sceneService.queryByLetterId(testLetterId, 1, 10);
        assertNotNull(result);
        assertTrue(result.getTotal() > 0);
    }

    @Test
    public void testUpdate() {
        SceneCreateRequest request = new SceneCreateRequest();
        request.setId(testSceneId);
        request.setLetterId(testLetterId);
        request.setSceneName("更新后的场景");
        request.setBusinessScene("SCENE_PAYMENT");
        request.setDecisionLevel("DEC_EXEC");

        sceneService.update(request);

        PageResult<AuthSceneVO> result = sceneService.queryByLetterId(testLetterId, 1, 10);
        AuthSceneVO vo = result.getList().stream()
            .filter(v -> v.getId().equals(testSceneId))
            .findFirst()
            .orElse(null);
        assertNotNull(vo);
        assertEquals("更新后的场景", vo.getSceneName());
    }

    @Test
    public void testDelete() {
        sceneService.delete(testSceneId);

        PageResult<AuthSceneVO> result = sceneService.queryByLetterId(testLetterId, 1, 10);
        assertEquals(0, result.getTotal().longValue());
    }

    @Test
    public void testMatchScenes() {
        Map<String, Object> params = new HashMap<>();
        List<SceneMatchResult> results = sceneService.matchScenes(testLetterId, params);
        assertNotNull(results);
    }
}