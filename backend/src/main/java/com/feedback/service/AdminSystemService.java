package com.feedback.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.feedback.common.BusinessException;
import com.feedback.entity.Category;
import com.feedback.entity.Permission;
import com.feedback.mapper.CategoryMapper;
import com.feedback.mapper.PermissionMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 系统管理服务
 *
 * @author Claude
 * @since 2026-03-11
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AdminSystemService {

    private final CategoryMapper categoryMapper;
    private final PermissionMapper permissionMapper;

    /**
     * 创建类别
     */
    public Category createCategory(Category category) {
        categoryMapper.insert(category);
        log.info("类别创建成功：类别ID={}, 类别名称={}", category.getId(), category.getName());
        return category;
    }

    /**
     * 更新类别
     */
    public Category updateCategory(Category category) {
        Category existing = categoryMapper.selectById(category.getId());
        if (existing == null) {
            throw new BusinessException(404, "类别不存在");
        }
        categoryMapper.updateById(category);
        log.info("类别更新成功：类别ID={}", category.getId());
        return category;
    }

    /**
     * 删除类别
     */
    public void deleteCategory(Long categoryId) {
        Category category = categoryMapper.selectById(categoryId);
        if (category == null) {
            throw new BusinessException(404, "类别不存在");
        }
        categoryMapper.deleteById(categoryId);
        log.info("类别删除成功：类别ID={}", categoryId);
    }

    /**
     * 获取所有类别
     */
    public List<Category> getAllCategories() {
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(Category::getSort);
        return categoryMapper.selectList(wrapper);
    }

    /**
     * 分配权限
     */
    public void assignPermission(Long adminId, Long categoryId) {
        // 检查权限是否已存在
        LambdaQueryWrapper<Permission> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Permission::getAdminId, adminId)
                .eq(Permission::getCategoryId, categoryId);
        Permission existing = permissionMapper.selectOne(wrapper);

        if (existing != null) {
            log.warn("权限已存在：管理员ID={}, 类别ID={}", adminId, categoryId);
            return;
        }

        // 创建权限
        Permission permission = new Permission();
        permission.setAdminId(adminId);
        permission.setCategoryId(categoryId);
        permissionMapper.insert(permission);

        log.info("权限分配成功：管理员ID={}, 类别ID={}", adminId, categoryId);
    }

    /**
     * 移除权限
     */
    public void removePermission(Long adminId, Long categoryId) {
        LambdaQueryWrapper<Permission> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Permission::getAdminId, adminId)
                .eq(Permission::getCategoryId, categoryId);
        permissionMapper.delete(wrapper);

        log.info("权限移除成功：管理员ID={}, 类别ID={}", adminId, categoryId);
    }

    /**
     * 获取管理员的权限类别
     */
    public List<Permission> getAdminPermissions(Long adminId) {
        LambdaQueryWrapper<Permission> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Permission::getAdminId, adminId);
        return permissionMapper.selectList(wrapper);
    }

}
