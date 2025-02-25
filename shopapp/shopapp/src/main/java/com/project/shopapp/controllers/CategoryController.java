package com.project.shopapp.controllers;


import com.project.shopapp.dtos.CategoryDTO;
import com.project.shopapp.models.Category;
import com.project.shopapp.services.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/categories") //http://localhost:8088/api/v1/categorie
//@RequiredArgsConstructor
@RequiredArgsConstructor
//@Validated // bỏ valid ngoài để các hàm trong xử lý valid
public class CategoryController {
    private final CategoryService categoryService;

    @PostMapping("")
    // Neu tham so truyen vao la 1 object => Data Transfer Object = Request Object
    public ResponseEntity<?> createdCategories
            (
                    @Valid @RequestBody CategoryDTO categoryDTO,
                    BindingResult result
            ) {
        if (result.hasErrors()){
//            dùng stream duyệt qua 1 danh sách để ánh sạ đến  nào đó và biến đổi k
            List<String> errorMessages = result.getFieldErrors()
                    .stream()
                    .map(FieldError:: getDefaultMessage)
                    .toList();
            return ResponseEntity.badRequest().body(errorMessages);
        }
        categoryService.createdCategory(categoryDTO);
        return ResponseEntity.ok("this is insert category" + categoryDTO);
    }

    // Hien thi tat ca cac danh muc
    @GetMapping("") //http://localhost:8088/api/v1/categories?page=1&limit=10
    public ResponseEntity<List<Category>> getAllCategories(
            @RequestParam("page") int page,
            @RequestParam("limit") int limit
    ) {
        List<Category> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(categories);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateCategories(
            @PathVariable Long id,
            @Valid @RequestBody CategoryDTO categoryDTO
    ) {
        categoryService.updateCategory(id, categoryDTO);
        return ResponseEntity.ok("Update category successfully");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCategories(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.ok("Delete category successfully");
    }
}
