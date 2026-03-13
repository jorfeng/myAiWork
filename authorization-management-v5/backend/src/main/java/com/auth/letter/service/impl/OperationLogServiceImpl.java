package com.auth.letter.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.auth.letter.dao.AuthOperationLogDao;
import com.auth.letter.entity.AuthOperationLog;
import com.auth.letter.service.OperationLogService;
import com.auth.letter.vo.AuthOperationLogVO;
import com.auth.letter.vo.PageResult;

@Service
public class OperationLogServiceImpl implements OperationLogService {

    @Autowired
    private AuthOperationLogDao operationLogDao;

    @Override
    public PageResult<AuthOperationLogVO> queryByLetterId(Long letterId, Integer pageNum, Integer pageSize) {
        Integer offset = (pageNum - 1) * pageSize;
        List<AuthOperationLog> list = operationLogDao.selectByLetterId(letterId, offset, pageSize);
        Long total = operationLogDao.countByLetterId(letterId);

        List<AuthOperationLogVO> voList = list.stream()
            .map(this::convertToVO)
            .collect(Collectors.toList());

        return new PageResult<>(voList, total, pageNum, pageSize);
    }

    @Override
    @Transactional
    public void log(Long letterId, String operation, String operator) {
        AuthOperationLog log = new AuthOperationLog();
        log.setLetterId(letterId);
        log.setOperation(operation);
        log.setOperator(operator);
        log.setOperationTime(LocalDateTime.now());
        operationLogDao.insert(log);
    }

    private AuthOperationLogVO convertToVO(AuthOperationLog log) {
        AuthOperationLogVO vo = new AuthOperationLogVO();
        vo.setId(log.getId());
        vo.setLetterId(log.getLetterId());
        vo.setOperation(log.getOperation());
        vo.setOperator(log.getOperator());
        vo.setOperationTime(log.getOperationTime());
        return vo;
    }
}