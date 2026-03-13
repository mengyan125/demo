package com.feedback.dto;

import lombok.Data;

/**
 * 反馈查询请求 DTO
 *
 * @author Claude
 * @since 2026-03-12
 */
@Data
public class FeedbackQueryRequest {
    private String status;
    private Long categoryId;
    private String keyword;
    private int pageNum = 1;
    private int pageSize = 10;
}
