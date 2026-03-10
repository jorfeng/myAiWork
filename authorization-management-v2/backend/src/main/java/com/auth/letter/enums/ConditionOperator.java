package com.auth.letter.enums;

/**
 * Condition Operator
 */
public enum ConditionOperator {
    EQ("等于"),
    NE("不等于"),
    GT("大于"),
    GE("大于等于"),
    LT("小于"),
    LE("小于等于"),
    IN("包含"),
    NOT_IN("不包含"),
    LIKE("模糊匹配");

    private final String description;

    ConditionOperator(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}