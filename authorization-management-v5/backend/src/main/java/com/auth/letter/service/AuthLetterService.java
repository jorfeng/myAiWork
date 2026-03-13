package com.auth.letter.service;

import java.util.List;
import com.auth.letter.vo.AuthLetterVO;
import com.auth.letter.vo.AuthLetterQueryRequest;
import com.auth.letter.vo.AuthLetterCreateRequest;
import com.auth.letter.vo.PageResult;

public interface AuthLetterService {
    PageResult<AuthLetterVO> queryList(AuthLetterQueryRequest request);

    AuthLetterVO getDetail(Long id);

    Long create(AuthLetterCreateRequest request);

    void update(AuthLetterCreateRequest request);

    void delete(Long id);

    void publish(Long id);

    void activate(Long id);

    void deactivate(Long id);

    void deleteBatch(List<Long> ids);
}