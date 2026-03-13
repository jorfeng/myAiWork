package com.auth.letter.dao;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.auth.letter.entity.AuthScene;

public interface AuthSceneDao {
    List<AuthScene> selectByLetterId(@Param("letterId") Long letterId,
                                      @Param("offset") Integer offset,
                                      @Param("pageSize") Integer pageSize);

    Long countByLetterId(@Param("letterId") Long letterId);

    AuthScene selectById(@Param("id") Long id);

    Long insert(AuthScene scene);

    Integer update(AuthScene scene);

    Integer deleteById(@Param("id") Long id);

    Integer deleteByIds(@Param("ids") List<Long> ids);

    List<AuthScene> selectByLetterIdForMatch(@Param("letterId") Long letterId);
}