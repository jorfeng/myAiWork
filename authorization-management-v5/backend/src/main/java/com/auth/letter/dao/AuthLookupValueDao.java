package com.auth.letter.dao;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.auth.letter.entity.AuthLookupValue;

public interface AuthLookupValueDao {
    List<AuthLookupValue> selectByTypeCode(@Param("typeCode") String typeCode);

    List<AuthLookupValue> selectByTypeCodeAndParentId(@Param("typeCode") String typeCode,
                                                       @Param("parentId") Long parentId);

    AuthLookupValue selectById(@Param("id") Long id);

    List<AuthLookupValue> selectByTypeCodeAndValues(@Param("typeCode") String typeCode,
                                                      @Param("values") List<String> values);
}