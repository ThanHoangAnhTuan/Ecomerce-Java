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
        productDto.setInventory(product.getInventory());
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
        orderDto.setSeller(mapUserToUserDto(order.getSeller()));
        orderDto.setBuyer(mapUserToUserDto(order.getBuyer()));
        return orderDto;
    }
}
