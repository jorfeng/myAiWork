package com.auth.letter.dao;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.auth.letter.entity.AuthLetter;

public interface AuthLetterDao {
    List<AuthLetter> selectList(@Param("name") String name,
                                 @Param("authTargetLevel") String authTargetLevel,
                                 @Param("applicableRegion") String applicableRegion,
                                 @Param("authPublishLevel") String authPublishLevel,
                                 @Param("authPublishOrg") String authPublishOrg,
                                 @Param("publishYear") Integer publishYear,
                                 @Param("status") String status,
                                 @Param("offset") Integer offset,
                                 @Param("pageSize") Integer pageSize);

    Long countList(@Param("name") String name,
                   @Param("authTargetLevel") String authTargetLevel,
                   @Param("applicableRegion") String applicableRegion,
                   @Param("authPublishLevel") String authPublishLevel,
                   @Param("authPublishOrg") String authPublishOrg,
                   @Param("publishYear") Integer publishYear,
                   @Param("status") String status);

    AuthLetter selectById(@Param("id") Long id);

    Long insert(AuthLetter letter);

    Integer update(AuthLetter letter);

    Integer deleteById(@Param("id") Long id);

    Integer updateStatus(@Param("id") Long id, @Param("status") String status);
}