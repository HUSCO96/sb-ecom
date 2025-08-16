package com.ecommerce.sb_ecom.controller;


import com.ecommerce.sb_ecom.config.AppConstants;
import com.ecommerce.sb_ecom.model.Category;
import com.ecommerce.sb_ecom.payload.CategoryDTO;
import com.ecommerce.sb_ecom.payload.CategoryResponse;
import com.ecommerce.sb_ecom.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/public/categories")
    public ResponseEntity<CategoryResponse> getAllCategories(@RequestParam(defaultValue = AppConstants.PAGE_NUMBER) Integer pageNumber,
                                                             @RequestParam(defaultValue = AppConstants.PAGE_SIZE) Integer pageSize,
                                                             @RequestParam(defaultValue = AppConstants.SORT_CATEGORIES_BY) String sortBy,
                                                             @RequestParam(defaultValue = AppConstants.SORT_DIR) String sortOrder){
        CategoryResponse categoryResponse = categoryService.getAllCategories(pageNumber, pageSize, sortBy, sortOrder);
        return new ResponseEntity<>(categoryResponse, HttpStatus.OK);
    }

    @PostMapping("/public/categories")
    //@RequestMapping(value = "/public/categories", method = RequestMethod.POST)
    public ResponseEntity<CategoryDTO> createCategory(@Valid @RequestBody CategoryDTO categoryDTO){
        CategoryDTO savedCategory = categoryService.createCategory(categoryDTO);
        return new ResponseEntity<>(savedCategory, HttpStatus.CREATED);
    }

    @DeleteMapping("/admin/categories/{categoryId}")
    public ResponseEntity<CategoryDTO> deleteCategory(@PathVariable Long categoryId){
           CategoryDTO categoryDTO = categoryService.deleteCategory(categoryId);
            return ResponseEntity.status(HttpStatus.OK).body(categoryDTO);

    }


    @PutMapping("/public/categories/{categoryId}")
    public ResponseEntity<CategoryDTO> updateCategory(@Valid @RequestBody CategoryDTO categoryDTO,
                                                 @PathVariable Long categoryId){

            CategoryDTO savedCategory = categoryService.updateCategory(categoryDTO, categoryId);
            return new ResponseEntity<>(savedCategory, HttpStatus.OK);

    }
}
