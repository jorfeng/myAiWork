package com.auth.letter.service;

import com.auth.letter.dto.AuthLetterListVO;
import com.auth.letter.dto.AuthLetterQueryDTO;
import com.auth.letter.dto.PageResult;

import java.util.List;

/**
 * Authorization Letter Service Interface
 */
public interface AuthLetterService {

    /**
     * Query authorization letters with pagination
     * @param queryDTO query parameters
     * @return paginated result
     */
    PageResult<AuthLetterListVO> queryList(AuthLetterQueryDTO queryDTO);

    /**
     * Batch publish authorization letters (DRAFT -> PUBLISHED)
     * @param ids authorization letter ids
     * @return number of affected rows
     */
    int batchPublish(List<Long> ids);

    /**
     * Batch expire authorization letters (PUBLISHED -> EXPIRED)
     * @param ids authorization letter ids
     * @return number of affected rows
     */
    int batchExpire(List<Long> ids);

    /**
     * Batch delete authorization letters (only DRAFT status)
     * @param ids authorization letter ids
     * @return number of affected rows
     */
    int batchDelete(List<Long> ids);

    /**
     * Get lookup values by code
     * TODO: Implement lookup service integration
     * @param code lookup code
     * @return list of lookup values
     */
    List<LookupValue> getLookupValues(String code);

    /**
     * Get organization tree
     * TODO: Implement organization tree from lookup service
     * @return organization tree structure
     */
    Object getOrgTree();

    /**
     * Lookup Value inner class
     */
    record LookupValue(String code, String name, String parentCode) {}
}