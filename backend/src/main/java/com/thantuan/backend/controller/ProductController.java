package com.thantuan.backend.controller;

import com.thantuan.backend.dto.CreateProductDtoRequest;
import com.thantuan.backend.dto.Response;
import com.thantuan.backend.dto.UpdateProductDtoRequest;
import com.thantuan.backend.service.ProductService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Slf4j
@SecurityRequirement(name = "bearerAuth")

public class ProductController {
    private final ProductService productService;

    @GetMapping("/get-all-products")
    public ResponseEntity<Response> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @GetMapping("/get-product-by-id/{productId}")
    public ResponseEntity<Response> getProductById(@PathVariable Long productId) {
        return ResponseEntity.ok(productService.getProductById(productId));
    }

    @GetMapping("/get-product-by-category-id/{categoryId}")
    public ResponseEntity<Response> getProductByCategoryId(@PathVariable Long categoryId) {
        return ResponseEntity.ok(productService.getProductsByCategory(categoryId));
    }

    @GetMapping("/search-product")
    public ResponseEntity<Response> searchProduct(@RequestParam("keyword") String value) {
        return ResponseEntity.ok(productService.searchProduct(value));
    }

    @GetMapping("/get-product-by-seller-id")
    @PreAuthorize("hasAuthority('SELLER')")
    public ResponseEntity<Response> getProductBySellerId() {
        return ResponseEntity.ok(productService.getProductBySellerId());
    }

    @PostMapping("/create-product")
    @PreAuthorize("hasAuthority('SELLER')")
    public ResponseEntity<Response> createProduct(@ModelAttribute @Valid CreateProductDtoRequest createProductDto)
            throws IOException {
        return ResponseEntity.ok(productService.createProduct(createProductDto));
    }

    @PatchMapping("/update-product/{productId}")
    @PreAuthorize("hasAuthority('SELLER')")
    public ResponseEntity<Response> updateProduct(@PathVariable Long productId,
                                                  @ModelAttribute @Valid UpdateProductDtoRequest updateProductDto)
            throws IOException, IllegalAccessException {
        return ResponseEntity.ok(
                productService.updateProduct(productId, updateProductDto));
    }

    @DeleteMapping("/delete-product/{productId}")
    @PreAuthorize("hasAuthority('SELLER')")
    public ResponseEntity<Response> deleteProduct(@PathVariable Long productId)
            throws IllegalAccessException {
        return ResponseEntity.ok(productService.deleteProduct(productId));
    }
}
