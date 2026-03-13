package com.auth.letter.dao;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.auth.letter.entity.AuthRuleParam;

public interface AuthRuleParamDao {
    List<AuthRuleParam> selectList(@Param("name") String name,
                                    @Param("nameEn") String nameEn,
                                    @Param("status") String status,
                                    @Param("offset") Integer offset,
                                    @Param("pageSize") Integer pageSize);

    Long countList(@Param("name") String name,
                   @Param("nameEn") String nameEn,
                   @Param("status") String status);

    AuthRuleParam selectById(@Param("id") Long id);

    Long insert(AuthRuleParam ruleParam);

    Integer update(AuthRuleParam ruleParam);

    Integer updateStatus(@Param("id") Long id, @Param("status") String status);

    Integer updateStatusByIds(@Param("ids") List<Long> ids, @Param("status") String status);

    List<AuthRuleParam> selectAllActive();
}