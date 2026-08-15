package com.ceygreen.ecommerce.service;

import com.ceygreen.ecommerce.dto.ProductResponse;
import com.ceygreen.ecommerce.entity.Product;
import com.ceygreen.ecommerce.entity.ProductListingStatus;

public final class ProductMapper {

    private ProductMapper() {}

    public static ProductResponse toResponse(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getFarmerId(),
                product.getCropName(),
                product.getQuantity(),
                product.getUnitPrice(),
                product.getHarvestDate(),
                product.getLocation(),
                product.getDescription(),
                product.getImageUrl(),
                product.getCreatedAt(),
                product.isActive(),
                deriveStatus(product));
    }

    public static ProductListingStatus deriveStatus(Product product) {
        if (!product.isActive()) {
            return ProductListingStatus.INACTIVE;
        }
        if (product.getQuantity() <= 0) {
            return ProductListingStatus.OUT_OF_STOCK;
        }
        return ProductListingStatus.ACTIVE;
    }
}
