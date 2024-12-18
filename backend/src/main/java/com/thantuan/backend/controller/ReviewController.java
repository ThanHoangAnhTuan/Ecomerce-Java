package com.thantuan.backend.controller;

import com.thantuan.backend.dto.Response;
import com.thantuan.backend.dto.ReviewDtoRequest;
import com.thantuan.backend.service.ReviewService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/review")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;

    @PostMapping("/add-review")
    public ResponseEntity<Response> addReview(@RequestBody @Valid ReviewDtoRequest reviewDto) {
        return ResponseEntity.ok(reviewService.addReview(reviewDto));
    }

    @GetMapping("/get-all-review-by-product-id/{productId}")
    public ResponseEntity<Response> addReview(@PathVariable Long productId) {
        return ResponseEntity.ok(reviewService.getReviewByProduct(productId));
    }
}
