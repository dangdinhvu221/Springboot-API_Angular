package com.project.shopapp.services.imp;

import com.project.shopapp.dtos.CategoryDTO;
import com.project.shopapp.models.Category;

import java.util.List;
public interface ICategoryService {
    Category createdCategory(CategoryDTO category);
    Category getCategoryById(Long id);
    List<Category> getAllCategories();
    Category updateCategory(Long category_id, CategoryDTO category);
    void deleteCategory(Long id);
}
