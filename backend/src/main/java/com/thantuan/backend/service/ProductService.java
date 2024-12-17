package com.thantuan.backend.service;

import com.thantuan.backend.dto.CreateProductDtoRequest;
import com.thantuan.backend.dto.ProductDto;
import com.thantuan.backend.dto.Response;
import com.thantuan.backend.dto.UpdateProductDtoRequest;
import com.thantuan.backend.entity.Category;
import com.thantuan.backend.entity.Product;
import com.thantuan.backend.exception.CategoryNotFoundException;
import com.thantuan.backend.exception.FileNotNullException;
import com.thantuan.backend.exception.ProductNotFoundException;
import com.thantuan.backend.mapper.EntityDtoMapper;
import com.thantuan.backend.repository.ICategoryRepo;
import com.thantuan.backend.repository.IProductRepo;
import com.thantuan.backend.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.OK;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    private final IProductRepo productRepo;
    private final ICategoryRepo categoryRepo;
    private final EntityDtoMapper entityDtoMapper;
    private final S3Service s3Service;

    public Response getAllProducts() {
        List<Product> productList = productRepo.findAll();
        List<ProductDto> productDtoList = productList.stream()
                                                    .map(entityDtoMapper::mapProductToProductDto)
                                                    .collect(Collectors.toList());
        return Response.builder()
                    .status(HttpStatus.OK.value())
                    .message("Get all products successfully")
                    .productList(productDtoList)
                    .build();
    }

    public Response createProduct(CreateProductDtoRequest createProductDto)
            throws IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal user = (UserPrincipal) authentication.getPrincipal();
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        Category category = categoryRepo.findById(createProductDto.getCategoryId())
                .orElseThrow(
                        () -> new CategoryNotFoundException("Category not found"));
        if (createProductDto.getFile() == null && createProductDto.getFile()
                .isEmpty()) {
            throw new FileNotNullException("File is empty");
        }
        Product product = Product.builder()
                                .name(createProductDto.getName())
                                .nameWithoutAccent(removeVietnameseTones(createProductDto.getName()))
                                .description(createProductDto.getDescription())
                                .descriptionWithoutAccent(removeVietnameseTones(createProductDto.getDescription()))
                                .price(createProductDto.getPrice())
                                .image(s3Service.uploadFile(createProductDto.getFile()))
                                .stock(createProductDto.getInventory())
                                .quantity(0)
                                .category(category)
                                .user(user.getUser())
                                .build();
        productRepo.save(product);
        ProductDto productDto = entityDtoMapper.mapProductToProductDto(product);
        return Response.builder()
                    .status(HttpStatus.CREATED.value())
                    .message("Product created successfully")
                    .product(productDto)
                    .build();
    }

    private String removeVietnameseTones(String str) {
        String[] vietnameseChars = {
                "à", "á", "ả", "ã", "ạ", "â", "ấ", "ầ", "ẩ", "ẫ", "ậ",
                "ă", "ắ", "ằ", "ẳ", "ẵ", "ặ", "è", "é", "ẻ", "ẽ", "ẹ", "ê", "ế", "ề", "ể", "ễ", "ệ",
                "ì", "í", "ỉ", "ĩ", "ị", "ò", "ó", "ỏ", "õ", "ọ", "ô", "ố", "ồ", "ổ", "ỗ", "ộ",
                "ơ", "ớ", "ờ", "ở", "ỡ", "ợ", "ù", "ú", "ủ", "ũ", "ụ", "ư", "ứ", "ừ", "ử", "ữ", "ự",
                "ỳ", "ý", "ỷ", "ỹ", "ỵ", "Đ", "đ"
        };

        String[] noTonesChars = {
                "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a",
                "a", "a", "a", "a", "a", "a", "e", "e", "e", "e", "e", "e", "e", "e", "e", "e", "e",
                "i", "i", "i", "i", "i", "o", "o", "o", "o", "o", "o", "o", "o", "o", "o", "o",
                "o", "o", "o", "u", "u", "u", "u", "u", "u", "u", "u", "u", "u", "u", "u", "u", "u",
                "y", "y", "y", "y", "y", "d", "d"
        };

        for (int i = 0; i < vietnameseChars.length; i++) {
            str = str.replace(vietnameseChars[i], noTonesChars[i]);
        }

        return str.toLowerCase();
    }

    public Response updateProduct(Long productId, UpdateProductDtoRequest updateProductDto)
            throws IOException, IllegalAccessException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal user = (UserPrincipal) authentication.getPrincipal();
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        Product product = productRepo.findById(productId)
                                    .orElseThrow(() -> new ProductNotFoundException("Product not found"));
        Category category = categoryRepo.findById(updateProductDto.getCategoryId())
                                        .orElseThrow(() -> new CategoryNotFoundException("Category not found"));
        if (!Objects.equals(product.getUser().getId(), user.getId())) {
            throw new IllegalAccessException("You can not update this product");
        }
        if (updateProductDto.getFile() != null && !updateProductDto.getFile().isEmpty()) {
            product.setImage(s3Service.uploadFile(updateProductDto.getFile()));
        }
        product.setCategory(category);
        product.setName(updateProductDto.getName());
        product.setPrice(updateProductDto.getPrice());
        product.setDescription(updateProductDto.getDescription());
        product.setStock(updateProductDto.getInventory());
        Product productSaved = productRepo.save(product);
        ProductDto productDto = entityDtoMapper.mapProductToProductDto(productSaved);
        return Response.builder()
                    .status(OK.value())
                    .message("Product updated successfully")
                    .product(productDto)
                    .build();
    }

    public Response deleteProduct(Long productId) throws IllegalAccessException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal user = (UserPrincipal) authentication.getPrincipal();
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        Product product = productRepo.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product not found"));
        if (!Objects.equals(user.getId(), product.getUser().getId())) {
            throw new IllegalAccessException("You can not delete product");
        }
        productRepo.delete(product);
        return Response.builder()
                    .status(OK.value())
                    .message("Product deleted created")
                    .build();
    }

    public Response getProductById(Long productId) {
        Product product = productRepo.findById(productId)
                                    .orElseThrow(() -> new ProductNotFoundException("Product not found"));
        ProductDto productDto = entityDtoMapper.mapProductToProductDto(product);
        return Response.builder()
                    .status(OK.value())
                    .product(productDto)
                    .build();
    }

    public Response getProductsByCategory(Long categoryId) {
        categoryRepo.findById(categoryId)
                    .orElseThrow(() -> new CategoryNotFoundException("Category not found"));
        List<Product> productList = productRepo.findByCategoryId(categoryId);
        List<ProductDto> productDtoList = productList.stream()
                                                    .map(entityDtoMapper::mapProductToProductDto)
                                                    .collect(Collectors.toList());
        return Response.builder()
                    .status(OK.value())
                    .message("Get all products by category id successfully")
                    .productList(productDtoList)
                    .build();
    }

    public Response searchProduct(String searchValue) {
        String searchValueNoTones = removeVietnameseTones(searchValue);
        List<Product> productList = productRepo.
                findByNameWithoutAccentContainingIgnoreCaseOrDescriptionWithoutAccentContainingIgnoreCase(searchValueNoTones, searchValueNoTones);
        List<ProductDto> productDtoList = productList.stream()
                                                    .map(entityDtoMapper::mapProductToProductDto)
                                                    .collect(Collectors.toList());
        return Response.builder()
                    .status(OK.value())
                    .productList(productDtoList)
                    .build();
    }

    public Response getProductBySellerId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal user = (UserPrincipal) authentication.getPrincipal();
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        List<Product> productList = productRepo.getProductsByUserId(user.getId());
        List<ProductDto> productDtoList = productList.stream()
                                                    .map(entityDtoMapper::mapProductToProductDto)
                                                    .collect(Collectors.toList());
        return Response.builder()
                    .status(OK.value())
                    .productList(productDtoList)
                    .build();
    }
}
