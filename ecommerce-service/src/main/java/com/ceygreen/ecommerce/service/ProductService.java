package com.ceygreen.ecommerce.service;

import com.ceygreen.ecommerce.common.ApiException;
import com.ceygreen.ecommerce.dto.ProductRequest;
import com.ceygreen.ecommerce.dto.ProductResponse;
import com.ceygreen.ecommerce.model.Product;
import com.ceygreen.ecommerce.repository.ProductRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ProductService {
    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) { this.productRepository = productRepository; }

    public List<ProductResponse> listProducts() {
        return productRepository.findByAvailableTrue().stream().map(this::toResponse).toList();
    }

    public ProductResponse getProduct(Long id) {
        return toResponse(productRepository.findById(id)
                .orElseThrow(() -> ApiException.notFound("Product not found: " + id)));
    }

    public ProductResponse createProduct(ProductRequest request) {
        Product p = new Product();
        p.setName(request.name()); p.setDescription(request.description());
        p.setFarmerId(request.farmerId()); p.setPrice(request.price());
        p.setQuantity(request.quantity()); p.setCropType(request.cropType());
        return toResponse(productRepository.save(p));
    }

    public ProductResponse updateProduct(Long id, ProductRequest request) {
        Product p = productRepository.findById(id)
                .orElseThrow(() -> ApiException.notFound("Product not found: " + id));
        if (request.name() != null) p.setName(request.name());
        if (request.price() != null) p.setPrice(request.price());
        p.setQuantity(request.quantity());
        return toResponse(productRepository.save(p));
    }

    private ProductResponse toResponse(Product p) {
        return new ProductResponse(p.getId(), p.getName(), p.getDescription(), p.getFarmerId(),
                p.getPrice(), p.getQuantity(), p.getCropType(), p.isAvailable(), p.getCreatedAt());
    }
}
