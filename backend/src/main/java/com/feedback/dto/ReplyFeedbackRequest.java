package com.feedback.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 回复反馈请求 DTO
 *
 * @author Claude
 * @since 2026-03-11
 */
@Data
public class ReplyFeedbackRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 反馈ID
     */
    @NotNull(message = "反馈ID不能为空")
    private Long feedbackId;

    /**
     * 回复内容
     */
    @NotBlank(message = "回复内容不能为空")
    private String content;

}
