package com.feedback.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 回复实体
 *
 * @author Claude
 * @since 2026-03-11
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("tb_reply")
public class Reply extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 反馈ID
     */
    private Long feedbackId;

    /**
     * 管理员ID
     */
    private Long adminId;

    /**
     * 管理员姓名（冗余）
     */
    private String adminName;

    /**
     * 回复内容
     */
    private String content;

}
