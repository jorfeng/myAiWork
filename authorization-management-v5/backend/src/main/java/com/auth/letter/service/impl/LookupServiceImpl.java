package com.auth.letter.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.auth.letter.dao.AuthLookupValueDao;
import com.auth.letter.entity.AuthLookupValue;
import com.auth.letter.service.LookupService;
import com.auth.letter.vo.LookupValueVO;

@Service
public class LookupServiceImpl implements LookupService {

    @Autowired
    private AuthLookupValueDao lookupValueDao;

    @Override
    public List<LookupValueVO> getLookupValues(String typeCode) {
        List<AuthLookupValue> allValues = lookupValueDao.selectByTypeCode(typeCode);
        return buildTree(allValues, null);
    }

    @Override
    public String getLabelsByValues(String typeCode, List<String> values) {
        if (values == null || values.isEmpty()) {
            return "";
        }
        List<AuthLookupValue> lookupValues = lookupValueDao.selectByTypeCodeAndValues(typeCode, values);
        return lookupValues.stream()
            .map(AuthLookupValue::getLabel)
            .collect(Collectors.joining(","));
    }

    private List<LookupValueVO> buildTree(List<AuthLookupValue> allValues, Long parentId) {
        List<LookupValueVO> result = new ArrayList<>();

        for (AuthLookupValue value : allValues) {
            boolean isChild = (parentId == null && value.getParentId() == null) ||
                              (parentId != null && parentId.equals(value.getParentId()));

            if (isChild) {
                LookupValueVO vo = new LookupValueVO();
                vo.setId(value.getId());
                vo.setValue(value.getItemValue());
                vo.setLabel(value.getLabel());
                vo.setParentId(value.getParentId());
                vo.setChildren(buildTree(allValues, value.getId()));
                result.add(vo);
            }
        }

        return result;
    }
}