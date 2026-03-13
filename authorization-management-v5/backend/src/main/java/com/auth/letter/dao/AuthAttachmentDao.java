package com.auth.letter.dao;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.auth.letter.entity.AuthAttachment;

public interface AuthAttachmentDao {
    List<AuthAttachment> selectByLetterId(@Param("letterId") Long letterId,
                                           @Param("offset") Integer offset,
                                           @Param("pageSize") Integer pageSize);

    Long countByLetterId(@Param("letterId") Long letterId);

    AuthAttachment selectById(@Param("id") Long id);

    Long insert(AuthAttachment attachment);

    Integer deleteById(@Param("id") Long id);

    Integer deleteByIds(@Param("ids") List<Long> ids);
}