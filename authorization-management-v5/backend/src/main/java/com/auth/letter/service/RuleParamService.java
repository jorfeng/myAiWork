package com.auth.letter.service;

import java.util.List;
import com.auth.letter.vo.AuthRuleParamVO;
import com.auth.letter.vo.PageResult;
import com.auth.letter.vo.RuleParamQueryRequest;
import com.auth.letter.vo.RuleParamCreateRequest;

public interface RuleParamService {
    PageResult<AuthRuleParamVO> queryList(RuleParamQueryRequest request);

    Long create(RuleParamCreateRequest request);

    void update(RuleParamCreateRequest request);

    void activate(Long id);

    void deactivate(Long id);

    void activateBatch(List<Long> ids);

    void deactivateBatch(List<Long> ids);

    List<AuthRuleParamVO> getAllActive();
}