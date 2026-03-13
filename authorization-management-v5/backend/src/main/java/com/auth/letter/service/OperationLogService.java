package com.auth.letter.service;

import java.util.List;
import com.auth.letter.vo.AuthOperationLogVO;
import com.auth.letter.vo.PageResult;

public interface OperationLogService {
    PageResult<AuthOperationLogVO> queryByLetterId(Long letterId, Integer pageNum, Integer pageSize);

    void log(Long letterId, String operation, String operator);
}