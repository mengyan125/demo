package com.feedback.controller;

import com.feedback.common.ApiResponse;
import com.feedback.dto.CreateCategoryRequest;
import com.feedback.dto.UpdateCategoryRequest;
import com.feedback.entity.Category;
import com.feedback.entity.Permission;
import com.feedback.service.AdminSystemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 系统管理控制器
 *
 * @author Claude
 * @since 2026-03-11
 */
@Slf4j
@RestController
@RequestMapping("/admin/system")
@RequiredArgsConstructor
public class AdminSystemController {

    private final AdminSystemService adminSystemService;

    /**
     * 创建类别
     */
    @PostMapping("/category")
    public ApiResponse<Category> createCategory(@Validated @RequestBody CreateCategoryRequest request) {
        log.info("创建类别：{}", request.getName());
        Category category = new Category();
        category.setName(request.getName());
        category.setDescription(request.getDescription());
        category.setRequireTeacher(request.getRequireTeacher());
        category.setSort(request.getSort());
        category.setStatus(1);
        Category result = adminSystemService.createCategory(category);
        return ApiResponse.success("类别创建成功", result);
    }

    /**
     * 更新类别
     */
    @PutMapping("/category/{id}")
    public ApiResponse<Category> updateCategory(
            @PathVariable Long id,
            @Validated @RequestBody UpdateCategoryRequest request) {
        log.info("更新类别：ID={}", id);
        Category category = new Category();
        category.setId(id);
        category.setName(request.getName());
        category.setDescription(request.getDescription());
        category.setRequireTeacher(request.getRequireTeacher());
        category.setSort(request.getSort());
        Category result = adminSystemService.updateCategory(category);
        return ApiResponse.success("类别更新成功", result);
    }

    /**
     * 删除类别
     */
    @DeleteMapping("/category/{id}")
    public ApiResponse<Void> deleteCategory(@PathVariable Long id) {
        log.info("删除类别：ID={}", id);
        adminSystemService.deleteCategory(id);
        return ApiResponse.success("类别删除成功", null);
    }

    /**
     * 获取所有类别
     */
    @GetMapping("/category/list")
    public ApiResponse<List<Category>> getAllCategories() {
        log.info("获取所有类别");
        List<Category> categories = adminSystemService.getAllCategories();
        return ApiResponse.success(categories);
    }

    /**
     * 分配权限
     */
    @PostMapping("/permission/assign")
    public ApiResponse<Void> assignPermission(
            @RequestParam Long adminId,
            @RequestParam Long categoryId) {
        log.info("分配权限：管理员ID={}, 类别ID={}", adminId, categoryId);
        adminSystemService.assignPermission(adminId, categoryId);
        return ApiResponse.success("权限分配成功", null);
    }

    /**
     * 移除权限
     */
    @DeleteMapping("/permission/remove")
    public ApiResponse<Void> removePermission(
            @RequestParam Long adminId,
            @RequestParam Long categoryId) {
        log.info("移除权限：管理员ID={}, 类别ID={}", adminId, categoryId);
        adminSystemService.removePermission(adminId, categoryId);
        return ApiResponse.success("权限移除成功", null);
    }

    /**
     * 获取管理员权限
     */
    @GetMapping("/permission/{adminId}")
    public ApiResponse<List<Permission>> getAdminPermissions(@PathVariable Long adminId) {
        log.info("获取管理员权限：管理员ID={}", adminId);
        List<Permission> permissions = adminSystemService.getAdminPermissions(adminId);
        return ApiResponse.success(permissions);
    }

}
