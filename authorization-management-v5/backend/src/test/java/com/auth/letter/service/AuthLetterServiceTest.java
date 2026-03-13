package com.auth.letter.service;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.auth.letter.vo.AuthLetterCreateRequest;
import com.auth.letter.vo.AuthLetterQueryRequest;
import com.auth.letter.vo.AuthLetterVO;
import com.auth.letter.vo.PageResult;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class AuthLetterServiceTest {

    @Autowired
    private AuthLetterService authLetterService;

    private Long testLetterId;

    @BeforeEach
    public void setUp() {
        AuthLetterCreateRequest request = new AuthLetterCreateRequest();
        request.setName("测试授权书");
        request.setSummary("测试摘要");
        request.setPublishYear(2024);
        testLetterId = authLetterService.create(request);
    }

    @Test
    public void testCreate() {
        assertNotNull(testLetterId);
        assertTrue(testLetterId > 0);
    }

    @Test
    public void testGetDetail() {
        AuthLetterVO vo = authLetterService.getDetail(testLetterId);
        assertNotNull(vo);
        assertEquals("测试授权书", vo.getName());
        assertEquals("DRAFT", vo.getStatus());
    }

    @Test
    public void testQueryList() {
        AuthLetterQueryRequest request = new AuthLetterQueryRequest();
        request.setPageNum(1);
        request.setPageSize(10);

        PageResult<AuthLetterVO> result = authLetterService.queryList(request);
        assertNotNull(result);
        assertTrue(result.getTotal() > 0);
    }

    @Test
    public void testUpdate() {
        AuthLetterCreateRequest request = new AuthLetterCreateRequest();
        request.setId(testLetterId);
        request.setName("更新后的授权书");
        request.setSummary("更新后的摘要");

        authLetterService.update(request);

        AuthLetterVO vo = authLetterService.getDetail(testLetterId);
        assertEquals("更新后的授权书", vo.getName());
        assertEquals("更新后的摘要", vo.getSummary());
    }

    @Test
    public void testPublish() {
        authLetterService.publish(testLetterId);

        AuthLetterVO vo = authLetterService.getDetail(testLetterId);
        assertEquals("PUBLISHED", vo.getStatus());
    }

    @Test
    public void testDeactivate() {
        authLetterService.publish(testLetterId);
        authLetterService.deactivate(testLetterId);

        AuthLetterVO vo = authLetterService.getDetail(testLetterId);
        assertEquals("INVALID", vo.getStatus());
    }

    @Test
    public void testDelete() {
        authLetterService.delete(testLetterId);

        AuthLetterVO vo = authLetterService.getDetail(testLetterId);
        assertNull(vo);
    }
}