package com.thantuan.backend.service;

import com.thantuan.backend.dto.Response;
import com.thantuan.backend.dto.ReviewDtoRequest;
import com.thantuan.backend.dto.ReviewDtoResponse;
import com.thantuan.backend.entity.Product;
import com.thantuan.backend.entity.Review;
import com.thantuan.backend.exception.ProductNotFoundException;
import com.thantuan.backend.mapper.EntityDtoMapper;
import com.thantuan.backend.repository.IProductRepo;
import com.thantuan.backend.repository.IReviewRepo;
import com.thantuan.backend.security.UserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewService {
    private final IReviewRepo reviewRepo;
    private final IProductRepo productRepo;
    private final EntityDtoMapper mapper;

    public Response addReview(@Valid ReviewDtoRequest reviewDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal user = (UserPrincipal) authentication.getPrincipal();
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        Product product = productRepo.findById(reviewDto.getProductId())
                .orElseThrow(() -> new ProductNotFoundException("Product not found"));
        Review review = Review.builder().content(reviewDto.getContent())
                .user(user.getUser())
                .product(product)
                .rating(reviewDto.getRating())
                .build();
        Review savedReview = reviewRepo.save(review);
        ReviewDtoResponse reviewDtoResponse = mapper.mapReviewToReviewDtoResponse(savedReview);
        return Response.builder()
                .status(HttpStatus.CREATED.value())
                .message("Add review successfully")
                .review(reviewDtoResponse)
                .build();
    }

    public Response getReviewByProduct(Long productId) {
        List<Review> reviewList = reviewRepo.findByProductId(productId);
        List<ReviewDtoResponse> reviewDtoResponseList = mapper.mapReviewListToReviewDtoResponseList(reviewList);
        List<ReviewDtoResponse> sorted = reviewDtoResponseList.stream()
                .sorted(Comparator.comparing(ReviewDtoResponse::getCreateAt).reversed())
                .toList();
        System.out.println(sorted);
        return Response.builder()
                .status(HttpStatus.OK.value())
                .message("Get review successfully")
                .reviewList(sorted)
                .build();
    }
}
