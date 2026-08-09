package com.ceygreen.ecommerce.controller;

import com.ceygreen.ecommerce.dto.ProductCreateRequest;
import com.ceygreen.ecommerce.dto.ProductResponse;
import com.ceygreen.ecommerce.dto.ProductUpdateRequest;
import com.ceygreen.ecommerce.security.RequestIdentity;
import com.ceygreen.ecommerce.security.UserRole;
import com.ceygreen.ecommerce.service.ProductService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public ResponseEntity<List<ProductResponse>> listProducts(
            @RequestParam(required = false) String cropName,
            @RequestParam(required = false) String location) {
        return ResponseEntity.ok(productService.listProducts(cropName, location));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProduct(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProduct(id));
    }

    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(
            HttpServletRequest request,
            @Valid @RequestBody ProductCreateRequest body) {
        RequestIdentity.requireRole(request, UserRole.FARMER);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(productService.createProduct(RequestIdentity.requireFarmerId(request), body));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> updateProduct(
            HttpServletRequest request,
            @PathVariable Long id,
            @Valid @RequestBody ProductUpdateRequest body) {
        UserRole role = RequestIdentity.requireRole(request, UserRole.FARMER, UserRole.ADMIN);
        UUID farmerId = role == UserRole.ADMIN ? null : RequestIdentity.requireFarmerId(request);
        return ResponseEntity.ok(productService.updateProduct(id, farmerId, role, body));
    }
}