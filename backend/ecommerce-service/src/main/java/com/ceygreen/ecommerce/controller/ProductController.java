package com.ceygreen.ecommerce.controller;

import com.ceygreen.ecommerce.common.ApiException;
import com.ceygreen.ecommerce.dto.ProductCreateRequest;
import com.ceygreen.ecommerce.dto.ProductResponse;
import com.ceygreen.ecommerce.dto.ProductStatusUpdateRequest;
import com.ceygreen.ecommerce.dto.ProductUpdateRequest;
import com.ceygreen.ecommerce.dto.StockUpdateRequest;
import com.ceygreen.ecommerce.entity.ProductListingStatus;
import com.ceygreen.ecommerce.security.RequestIdentity;
import com.ceygreen.ecommerce.security.UserRole;
import com.ceygreen.ecommerce.service.ProductService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
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
    public ResponseEntity<Page<ProductResponse>> listProducts(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String cropName,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) Boolean inStock,
            @RequestParam(required = false) Boolean active,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String sort,
            Pageable pageable) {
        ProductListingStatus statusFilter = parseStatus(status);
        Boolean activeFilter = active != null ? active : Boolean.TRUE;
        return ResponseEntity.ok(productService.listProducts(
                q, cropName, location, minPrice, maxPrice, inStock, activeFilter, null, statusFilter, sort, pageable));
    }

    @GetMapping("/categories")
    public ResponseEntity<List<String>> listCategories() {
        return ResponseEntity.ok(productService.listCategories());
    }

    @GetMapping("/featured")
    public ResponseEntity<List<ProductResponse>> listFeatured(@RequestParam(defaultValue = "8") int limit) {
        return ResponseEntity.ok(productService.listFeatured(limit));
    }

    @GetMapping("/low-stock")
    public ResponseEntity<List<ProductResponse>> listLowStock(HttpServletRequest request) {
        RequestIdentity.requireRole(request, UserRole.FARMER);
        UUID farmerId = RequestIdentity.requireFarmerId(request);
        return ResponseEntity.ok(productService.listLowStock(farmerId));
    }

    @GetMapping("/farmer/{farmerId}")
    public ResponseEntity<Page<ProductResponse>> listFarmerProducts(
            HttpServletRequest request,
            @PathVariable UUID farmerId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String sort,
            Pageable pageable) {
        UserRole role = RequestIdentity.requireRole(request, UserRole.FARMER, UserRole.ADMIN);
        if (role != UserRole.ADMIN) {
            UUID callerFarmerId = RequestIdentity.requireFarmerId(request);
            if (!callerFarmerId.equals(farmerId)) {
                throw ApiException.forbidden("You may only view your own listings");
            }
        }
        return ResponseEntity.ok(productService.listFarmerProducts(
                farmerId, parseStatus(status), sort, pageable));
    }

    @GetMapping("/{id:\\d+}")
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

    @PutMapping("/{id:\\d+}")
    public ResponseEntity<ProductResponse> updateProduct(
            HttpServletRequest request,
            @PathVariable Long id,
            @Valid @RequestBody ProductUpdateRequest body) {
        UserRole role = RequestIdentity.requireRole(request, UserRole.FARMER, UserRole.ADMIN);
        UUID farmerId = role == UserRole.ADMIN ? null : RequestIdentity.requireFarmerId(request);
        return ResponseEntity.ok(productService.updateProduct(id, farmerId, role, body));
    }

    @DeleteMapping("/{id:\\d+}")
    public ResponseEntity<Void> deleteProduct(HttpServletRequest request, @PathVariable Long id) {
        UserRole role = RequestIdentity.requireRole(request, UserRole.FARMER, UserRole.ADMIN);
        UUID farmerId = role == UserRole.ADMIN ? null : RequestIdentity.requireFarmerId(request);
        productService.deleteProduct(id, farmerId, role);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id:\\d+}/stock")
    public ResponseEntity<ProductResponse> updateStock(
            HttpServletRequest request,
            @PathVariable Long id,
            @Valid @RequestBody StockUpdateRequest body) {
        UserRole role = RequestIdentity.requireRole(request, UserRole.FARMER, UserRole.ADMIN);
        UUID farmerId = role == UserRole.ADMIN ? null : RequestIdentity.requireFarmerId(request);
        return ResponseEntity.ok(productService.updateStock(id, farmerId, role, body));
    }

    @PatchMapping("/{id:\\d+}/status")
    public ResponseEntity<ProductResponse> updateStatus(
            HttpServletRequest request,
            @PathVariable Long id,
            @Valid @RequestBody ProductStatusUpdateRequest body) {
        UserRole role = RequestIdentity.requireRole(request, UserRole.FARMER, UserRole.ADMIN);
        UUID farmerId = role == UserRole.ADMIN ? null : RequestIdentity.requireFarmerId(request);
        return ResponseEntity.ok(productService.updateStatus(id, farmerId, role, body));
    }

    private static ProductListingStatus parseStatus(String status) {
        if (status == null || status.isBlank()) {
            return null;
        }
        return ProductListingStatus.valueOf(status.trim().toUpperCase());
    }
}
