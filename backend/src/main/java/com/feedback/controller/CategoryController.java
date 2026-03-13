package com.feedback.controller;

import com.feedback.common.ApiResponse;
import com.feedback.entity.Category;
import com.feedback.service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 类别控制器
 *
 * @author Claude
 * @since 2026-03-11
 */
@Slf4j
@RestController
@RequestMapping("/category")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    /**
     * 获取所有启用的类别
     */
    @GetMapping("/list")
    public ApiResponse<List<Category>> getCategories() {
        log.info("获取类别列表");
        List<Category> categories = categoryService.getEnabledCategories();
        return ApiResponse.success(categories);
    }

}
