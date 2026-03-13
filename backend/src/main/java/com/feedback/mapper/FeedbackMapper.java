package com.feedback.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.feedback.entity.Feedback;
import org.apache.ibatis.annotations.Mapper;

/**
 * 反馈 Mapper
 *
 * @author Claude
 * @since 2026-03-11
 */
@Mapper
public interface FeedbackMapper extends BaseMapper<Feedback> {

}
