package com.auth.letter.vo;

import java.util.List;

public class PageRequest {
    private Integer pageNum = 1;
    private Integer pageSize = 10;
    private String sortField;
    private String sortOrder;

    public Integer getPageNum() {
        return pageNum;
    }

    public void setPageNum(Integer pageNum) {
        this.pageNum = pageNum;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public String getSortField() {
        return sortField;
    }

    public void setSortField(String sortField) {
        this.sortField = sortField;
    }

    public String getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(String sortOrder) {
        this.sortOrder = sortOrder;
    }

    public Integer getOffset() {
        return (pageNum - 1) * pageSize;
    }
}