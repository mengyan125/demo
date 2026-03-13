package com.feedback.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 反馈统计 DTO
 *
 * @author Claude
 * @since 2026-03-12
 */
@Data
@AllArgsConstructor
public class FeedbackStatisticsDTO {
    private long pendingCount;
    private long processingCount;
    private long repliedCount;
    private long closedCount;
    private long totalCount;
}
