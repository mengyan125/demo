package com.feedback.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.feedback.entity.Permission;
import org.apache.ibatis.annotations.Mapper;

/**
 * 权限 Mapper
 *
 * @author Claude
 * @since 2026-03-11
 */
@Mapper
public interface PermissionMapper extends BaseMapper<Permission> {

}
