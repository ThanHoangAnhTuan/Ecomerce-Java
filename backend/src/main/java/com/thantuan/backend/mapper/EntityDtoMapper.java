package com.thantuan.backend.mapper;

import com.thantuan.backend.dto.*;
import com.thantuan.backend.entity.*;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class EntityDtoMapper {
    public UserDto mapUserToUserDto(User user) {
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setName(user.getName());
        userDto.setEmail(user.getEmail());
        userDto.setPhone(user.getPhone());
        userDto.setRoles(user.getRoles().stream().map(Role::getName).collect(Collectors.toSet()));
        userDto.setAddress(user.getAddress());
        userDto.setAuthProvider(user.getProvider().name());
        userDto.setEnabled(user.isEnabled());
        userDto.setAccountLocked(user.isAccountLocked());
        return userDto;
    }

    public ProductDto mapProductToProductDto(Product product) {
        ProductDto productDto = new ProductDto();
        productDto.setId(product.getId());
        productDto.setName(product.getName());
        productDto.setPrice(product.getPrice());
        productDto.setDescription(product.getDescription());
        productDto.setImage(product.getImage());
        productDto.setUser(mapUserToUserDto(product.getUser()));
        productDto.setStock(product.getStock());
        productDto.setCategory(mapCategoryToCategoryDto(product.getCategory()));
        return productDto;
    }

    public CategoryDtoResponse mapCategoryToCategoryDto(Category category) {
        CategoryDtoResponse categoryDto = new CategoryDtoResponse();
        categoryDto.setId(category.getId());
        categoryDto.setName(category.getName());
        categoryDto.setImage(String.valueOf(category.getImage()));
        return categoryDto;
    }

    public OrderItemDto mapOrderItemToOrderItemDto(OrderItem orderItem) {
        OrderItemDto orderItemDto = new OrderItemDto();
        orderItemDto.setId(orderItem.getId());
        orderItemDto.setQuantity(orderItem.getQuantity());
        orderItemDto.setPrice(orderItem.getPrice());
        orderItemDto.setProduct(mapProductToProductDto(orderItem.getProduct()));
        return orderItemDto;
    }

    public OrderDto mapOrderToOrderDto(Order order) {
        OrderDto orderDto = new OrderDto();
        orderDto.setId(order.getId());
        orderDto.setTotal(order.getTotal());
        orderDto.setStatus(order.getStatus().toString());
        List<OrderItemDto> orderItemDtoList = order.getOrderItemList()
                                                .stream()
                                                .map(this::mapOrderItemToOrderItemDto)
                                                .toList();
        orderDto.setOrderItemList(orderItemDtoList);
        orderDto.setCreateAt(order.getCreateAt());
        orderDto.setBuyer(mapUserToUserDto(order.getBuyer()));
        return orderDto;
    }

    public ReviewDtoResponse mapReviewToReviewDtoResponse(Review review) {
        ReviewDtoResponse reviewDtoResponse = new ReviewDtoResponse();
        reviewDtoResponse.setId(review.getId());
        reviewDtoResponse.setContent(review.getContent());
        reviewDtoResponse.setRating(review.getRating());
        reviewDtoResponse.setUser(mapUserToUserDto(review.getUser()));
        reviewDtoResponse.setProduct(mapProductToProductDto(review.getProduct()));
        reviewDtoResponse.setCreateAt(review.getCreateAt());
        return reviewDtoResponse;
    }

    public List<ReviewDtoResponse> mapReviewListToReviewDtoResponseList(List<Review> reviewList) {
        return reviewList.stream()
                .map(this::mapReviewToReviewDtoResponse)
                .toList();
    }
}
