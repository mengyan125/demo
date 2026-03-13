package com.feedback.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 备注实体
 *
 * @author Claude
 * @since 2026-03-11
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("tb_remark")
public class Remark extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 反馈ID
     */
    private Long feedbackId;

    /**
     * 发起人ID
     */
    private Long fromAdminId;

    /**
     * 发起人姓名（冗余）
     */
    private String fromAdminName;

    /**
     * 接收人ID（NULL表示所有管理员可见）
     */
    private Long toAdminId;

    /**
     * 接收人姓名（冗余）
     */
    private String toAdminName;

    /**
     * 备注内容
     */
    private String content;

    /**
     * 紧急程度：normal=普通，important=重要，urgent=紧急
     */
    private String priority;

    /**
     * 是否已读：1=是，0=否
     */
    private Integer isRead;

}
