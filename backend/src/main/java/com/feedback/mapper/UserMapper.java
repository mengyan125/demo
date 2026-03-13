package com.feedback.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.feedback.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户 Mapper
 *
 * @author Claude
 * @since 2026-03-11
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

}
