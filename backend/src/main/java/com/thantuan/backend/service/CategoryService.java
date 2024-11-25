package com.thantuan.backend.service;

import com.thantuan.backend.dto.CategoryDtoResponse;
import com.thantuan.backend.dto.Response;
import com.thantuan.backend.entity.Category;
import com.thantuan.backend.exception.CategoryAlreadyExistsException;
import com.thantuan.backend.exception.CategoryNameNotNullException;
import com.thantuan.backend.exception.CategoryNotFoundException;
import com.thantuan.backend.exception.FileNotNullException;
import com.thantuan.backend.mapper.EntityDtoMapper;
import com.thantuan.backend.repository.ICategoryRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@Service
@Slf4j
@RequiredArgsConstructor
public class CategoryService {
    private final ICategoryRepo categoryRepo;
    private final EntityDtoMapper entityDtoMapper;
    private final S3Service s3Service;

    public Response createCategory(String name, MultipartFile file)
            throws CategoryNameNotNullException, CategoryAlreadyExistsException, IOException {
        Category categoryAlreadyExists = categoryRepo.findByName(name);
        if (categoryAlreadyExists != null) {
            throw new CategoryAlreadyExistsException("Category has already exists");
        }
        if (name == null || name.isEmpty()) {
            throw new CategoryNameNotNullException("Category name is required!");
        }
        if (file == null || file.isEmpty()) {
            throw new FileNotNullException("File is required!");
        }
        String image = s3Service.uploadFile(file);
        Category category = Category.builder()
                                    .image(image)
                                    .name(name)
                                    .build();
        categoryRepo.save(category);
        CategoryDtoResponse categoryDto = entityDtoMapper.mapCategoryToCategoryDto(category);
        return Response.builder()
                    .status(CREATED.value())
                    .category(categoryDto)
                    .message("Category created successfully")
                    .build();
    }

    public Response updateCategory(Long categoryId, String name, MultipartFile file)
            throws IOException {
        Category category = categoryRepo.findById(categoryId)
                .orElseThrow(() -> new CategoryNotFoundException("Category not found"));
        if (name != null && !name.isEmpty()) {
            category.setName(name);
        }
        if (file != null && !file.isEmpty()) {
            category.setImage(s3Service.uploadFile(file));
        }
        Category categorySaved = categoryRepo.save(category);
        CategoryDtoResponse categoryDto = entityDtoMapper.mapCategoryToCategoryDto(categorySaved);
        return Response.builder()
                    .category(categoryDto)
                    .status(OK.value())
                    .message("Category updated successfully")
                    .build();
    }

    public Response getAllCategories() {
        List<Category> categoryList = categoryRepo.findAll();
        List<CategoryDtoResponse> categoryDtoList = categoryList.stream()
                                                                .map(entityDtoMapper::mapCategoryToCategoryDto)
                                                                .toList();
        return Response.builder()
                    .status(OK.value())
                    .categoryList(categoryDtoList)
                    .build();
    }

    public Response getCategoryById(Long categoryId) {
        Category category = categoryRepo.findById(categoryId)
                .orElseThrow(() -> new CategoryNotFoundException("Category not found"));
        CategoryDtoResponse categoryDto = entityDtoMapper.mapCategoryToCategoryDto(category);
        return Response.builder()
                    .status(OK.value())
                    .category(categoryDto)
                    .build();
    }

    public Response deleteCategory(Long categoryId) {
        Category category = categoryRepo.findById(categoryId)
                .orElseThrow(() -> new CategoryNotFoundException("Category not found"));
        categoryRepo.delete(category);
        return Response.builder()
                    .status(OK.value())
                    .message("Category was deleted successfully")
                    .build();
    }

    public Response initCategory(MultipartFile[] files) throws IOException {
        for (MultipartFile file : files) {
            String name = Objects.requireNonNull(file.getOriginalFilename()).split("\\.")[0];
            String image = s3Service.uploadFile(file);
            Category category = Category.builder()
                                        .name(name)
                                        .image(image)
                                        .build();
            categoryRepo.save(category);
        }
        return Response.builder()
                    .status(OK.value())
                    .message("Init category successfully")
                    .build();
    }
}
