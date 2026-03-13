package com.feedback.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 创建类别请求 DTO
 *
 * @author Claude
 * @since 2026-03-11
 */
@Data
public class CreateCategoryRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 类别名称
     */
    @NotBlank(message = "类别名称不能为空")
    private String name;

    /**
     * 类别描述
     */
    private String description;

    /**
     * 是否关联任课教师
     */
    @NotNull(message = "是否关联任课教师不能为空")
    private Integer requireTeacher;

    /**
     * 排序
     */
    @NotNull(message = "排序不能为空")
    private Integer sort;

}
