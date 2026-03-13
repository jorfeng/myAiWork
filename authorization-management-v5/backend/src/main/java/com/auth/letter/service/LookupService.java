package com.auth.letter.service;

import java.util.List;
import com.auth.letter.vo.LookupValueVO;

public interface LookupService {
    List<LookupValueVO> getLookupValues(String typeCode);

    String getLabelsByValues(String typeCode, List<String> values);
}