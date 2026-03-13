package com.feedback.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 反馈类别实体
 *
 * @author Claude
 * @since 2026-03-11
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("tb_category")
public class Category extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 类别名称
     */
    private String name;

    /**
     * 类别描述
     */
    private String description;

    /**
     * 是否关联任课教师：1=是，0=否
     */
    private Integer requireTeacher;

    /**
     * 排序（数字越小越靠前）
     */
    private Integer sort;

    /**
     * 状态：1=启用，0=禁用
     */
    private Integer status;

}
