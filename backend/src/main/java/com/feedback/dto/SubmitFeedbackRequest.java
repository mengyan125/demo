package com.feedback.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 提交反馈请求 DTO
 *
 * @author Claude
 * @since 2026-03-11
 */
@Data
public class SubmitFeedbackRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 反馈类别ID
     */
    @NotNull(message = "反馈类别不能为空")
    private Long categoryId;

    /**
     * 是否与任课教师相关：1=是，0=否
     */
    private Integer isTeacherRelated;

    /**
     * 教师ID（当isTeacherRelated=1时必填）
     */
    private Long teacherId;

    /**
     * 反馈主题
     */
    @NotBlank(message = "反馈主题不能为空")
    private String title;

    /**
     * 反馈内容
     */
    @NotBlank(message = "反馈内容不能为空")
    private String content;

    /**
     * 是否匿名：1=是，0=否
     */
    @NotNull(message = "是否匿名不能为空")
    private Integer isAnonymous;

}
