package com.auth.letter.service;

import java.util.List;
import com.auth.letter.vo.AuthAttachmentVO;
import com.auth.letter.vo.PageResult;

public interface AttachmentService {
    PageResult<AuthAttachmentVO> queryByLetterId(Long letterId, Integer pageNum, Integer pageSize);

    void delete(Long id);

    void deleteBatch(List<Long> ids);

    Long upload(Long letterId, String docName, String docType, String docId);
}