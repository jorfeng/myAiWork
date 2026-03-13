package com.auth.letter.dao;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.auth.letter.entity.AuthOperationLog;

public interface AuthOperationLogDao {
    List<AuthOperationLog> selectByLetterId(@Param("letterId") Long letterId,
                                             @Param("offset") Integer offset,
                                             @Param("pageSize") Integer pageSize);

    Long countByLetterId(@Param("letterId") Long letterId);

    Long insert(AuthOperationLog log);
}