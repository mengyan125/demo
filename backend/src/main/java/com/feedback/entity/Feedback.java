package com.feedback.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 反馈实体
 *
 * @author Claude
 * @since 2026-03-11
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("tb_feedback")
public class Feedback extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 学生ID
     */
    private Long studentId;

    /**
     * 学生姓名（冗余）
     */
    private String studentName;

    /**
     * 班级ID
     */
    private Long classId;

    /**
     * 班级名称（冗余）
     */
    private String className;

    /**
     * 类别ID
     */
    private Long categoryId;

    /**
     * 类别名称（冗余）
     */
    private String categoryName;

    /**
     * 是否与任课教师相关：1=是，0=否，NULL=该类别不关联教师
     */
    private Integer isTeacherRelated;

    /**
     * 教师ID（仅当isTeacherRelated=1时有值）
     */
    private Long teacherId;

    /**
     * 教师姓名（冗余）
     */
    private String teacherName;

    /**
     * 反馈主题
     */
    private String title;

    /**
     * 反馈内容
     */
    private String content;

    /**
     * 是否匿名：1=是，0=否
     */
    private Integer isAnonymous;

    /**
     * 状态：pending=待处理，processing=处理中，replied=已回复，closed=已关闭
     */
    private String status;

    /**
     * 更新时间
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

}
