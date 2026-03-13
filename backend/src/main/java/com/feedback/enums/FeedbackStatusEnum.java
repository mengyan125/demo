package com.feedback.enums;

/**
 * 反馈状态枚举
 *
 * @author Claude
 * @since 2026-03-12
 */
public enum FeedbackStatusEnum {
    PENDING("pending", "待处理"),
    PROCESSING("processing", "处理中"),
    REPLIED("replied", "已回复"),
    CLOSED("closed", "已关闭");

    private final String value;
    private final String label;

    FeedbackStatusEnum(String value, String label) {
        this.value = value;
        this.label = label;
    }

    public String getValue() {
        return value;
    }

    public String getLabel() {
        return label;
    }

    public static FeedbackStatusEnum fromValue(String value) {
        for (FeedbackStatusEnum status : FeedbackStatusEnum.values()) {
            if (status.value.equals(value)) {
                return status;
            }
        }
        return null;
    }
}
