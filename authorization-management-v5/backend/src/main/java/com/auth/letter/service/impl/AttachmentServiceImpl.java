package com.auth.letter.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.auth.letter.dao.AuthAttachmentDao;
import com.auth.letter.entity.AuthAttachment;
import com.auth.letter.service.AttachmentService;
import com.auth.letter.vo.AuthAttachmentVO;
import com.auth.letter.vo.PageResult;

@Service
public class AttachmentServiceImpl implements AttachmentService {

    @Autowired
    private AuthAttachmentDao attachmentDao;

    @Override
    public PageResult<AuthAttachmentVO> queryByLetterId(Long letterId, Integer pageNum, Integer pageSize) {
        Integer offset = (pageNum - 1) * pageSize;
        List<AuthAttachment> list = attachmentDao.selectByLetterId(letterId, offset, pageSize);
        Long total = attachmentDao.countByLetterId(letterId);

        List<AuthAttachmentVO> voList = list.stream()
            .map(this::convertToVO)
            .collect(Collectors.toList());

        return new PageResult<>(voList, total, pageNum, pageSize);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        attachmentDao.deleteById(id);
    }

    @Override
    @Transactional
    public void deleteBatch(List<Long> ids) {
        if (ids != null && !ids.isEmpty()) {
            attachmentDao.deleteByIds(ids);
        }
    }

    @Override
    @Transactional
    public Long upload(Long letterId, String docName, String docType, String docId) {
        AuthAttachment attachment = new AuthAttachment();
        attachment.setLetterId(letterId);
        attachment.setDocName(docName);
        attachment.setDocType(docType);
        attachment.setDocId(docId);
        attachment.setIsEncrypted(false);
        attachment.setCreatedBy("system");
        attachment.setCreatedTime(LocalDateTime.now());
        attachment.setUpdatedBy("system");
        attachment.setUpdatedTime(LocalDateTime.now());

        attachmentDao.insert(attachment);
        return attachment.getId();
    }

    private AuthAttachmentVO convertToVO(AuthAttachment attachment) {
        AuthAttachmentVO vo = new AuthAttachmentVO();
        vo.setId(attachment.getId());
        vo.setLetterId(attachment.getLetterId());
        vo.setDocName(attachment.getDocName());
        vo.setDocType(attachment.getDocType());
        vo.setDocId(attachment.getDocId());
        vo.setIsEncrypted(attachment.getIsEncrypted());
        vo.setCreatedBy(attachment.getCreatedBy());
        vo.setCreatedTime(attachment.getCreatedTime());
        vo.setUpdatedBy(attachment.getUpdatedBy());
        vo.setUpdatedTime(attachment.getUpdatedTime());
        return vo;
    }
}