package com.auth.letter.vo;

import java.util.List;

public class LookupValueVO {
    private Long id;
    private String value;
    private String label;
    private Long parentId;
    private List<LookupValueVO> children;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public List<LookupValueVO> getChildren() {
        return children;
    }

    public void setChildren(List<LookupValueVO> children) {
        this.children = children;
    }
}