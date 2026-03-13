package com.auth.letter.service;

import java.util.List;
import java.util.Map;
import com.auth.letter.vo.AuthSceneVO;
import com.auth.letter.vo.PageResult;
import com.auth.letter.vo.SceneCreateRequest;
import com.auth.letter.vo.SceneMatchResult;

public interface SceneService {
    PageResult<AuthSceneVO> queryByLetterId(Long letterId, Integer pageNum, Integer pageSize);

    Long create(SceneCreateRequest request);

    void update(SceneCreateRequest request);

    void delete(Long id);

    void deleteBatch(List<Long> ids);

    List<SceneMatchResult> matchScenes(Long letterId, Map<String, Object> params);
}