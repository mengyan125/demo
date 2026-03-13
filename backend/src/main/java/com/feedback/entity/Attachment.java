package com.feedback.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 附件实体
 *
 * @author Claude
 * @since 2026-03-11
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("tb_attachment")
public class Attachment extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 反馈ID
     */
    private Long feedbackId;

    /**
     * 文件名
     */
    private String fileName;

    /**
     * 文件存储路径
     */
    private String filePath;

    /**
     * 文件大小（字节）
     */
    private Long fileSize;

    /**
     * 文件类型（MIME Type）
     */
    private String fileType;

}
