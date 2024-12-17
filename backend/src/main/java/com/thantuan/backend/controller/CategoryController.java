package com.thantuan.backend.controller;

import com.thantuan.backend.dto.Response;
import com.thantuan.backend.exception.CategoryAlreadyExistsException;
import com.thantuan.backend.exception.CategoryNameNotNullException;
import com.thantuan.backend.service.CategoryService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/category")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")

public class CategoryController {
    private final CategoryService categoryService;

    @PostMapping("/create-category")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Response> createCategory(@RequestParam("name") String name,
                                                   @RequestParam("file") MultipartFile file)
            throws CategoryNameNotNullException, CategoryAlreadyExistsException, IOException {
        return ResponseEntity.ok(categoryService.createCategory(name, file));
    }

    @PostMapping("/init-category")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Response> initCategory(@RequestParam("files") MultipartFile[] files)
            throws IOException {
        return ResponseEntity.ok(categoryService.initCategory(files));
    }

    @GetMapping("/get-all-categories")
    public ResponseEntity<Response> getAllCategories() {
        return ResponseEntity.ok(categoryService.getAllCategories());
    }

    @GetMapping("/get-category-by-id/{categoryId}")
    public ResponseEntity<Response> getCategoryById(@PathVariable Long categoryId) {
        return ResponseEntity.ok(categoryService.getCategoryById(categoryId));
    }

    @PutMapping("/update-category-by-id/{categoryId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Response> updateCategory(@PathVariable Long categoryId,
                                                   @RequestParam(value = "name", required = false) String name,
                                                   @RequestParam(value = "file", required = false) MultipartFile file)
                                                    throws IOException {
        return ResponseEntity.ok(categoryService.updateCategory(categoryId, name, file));
    }

    @DeleteMapping("/delete-category-by-id/{categoryId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Response> deleteCategory(@PathVariable Long categoryId) {
        return ResponseEntity.ok(categoryService.deleteCategory(categoryId));
    }
}
