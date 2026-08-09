package com.ceygreen.ecommerce.service;

import com.ceygreen.ecommerce.common.ApiException;
import com.ceygreen.ecommerce.dto.ProductCreateRequest;
import com.ceygreen.ecommerce.dto.ProductResponse;
import com.ceygreen.ecommerce.dto.ProductUpdateRequest;
import com.ceygreen.ecommerce.entity.Product;
import com.ceygreen.ecommerce.repository.ProductRepository;
import com.ceygreen.ecommerce.security.UserRole;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final StockEventService stockEventService;

    public ProductService(ProductRepository productRepository, StockEventService stockEventService) {
        this.productRepository = productRepository;
        this.stockEventService = stockEventService;
    }

    public List<ProductResponse> listProducts(String cropName, String location) {
        boolean hasCrop = cropName != null && !cropName.isBlank();
        boolean hasLocation = location != null && !location.isBlank();

        List<Product> products;
        if (hasCrop && hasLocation) {
            products = productRepository.findByActiveTrueAndCropNameIgnoreCaseAndLocationIgnoreCase(
                    cropName.trim(), location.trim());
        } else if (hasCrop) {
            products = productRepository.findByActiveTrueAndCropNameIgnoreCase(cropName.trim());
        } else if (hasLocation) {
            products = productRepository.findByActiveTrueAndLocationIgnoreCase(location.trim());
        } else {
            products = productRepository.findByActiveTrue();
        }

        return products.stream().map(this::toResponse).toList();
    }

    public ProductResponse getProduct(Long id) {
        return toResponse(findProduct(id));
    }

    @Transactional
    public ProductResponse createProduct(UUID farmerId, ProductCreateRequest request) {
        Product product = new Product();
        product.setFarmerId(farmerId);
        product.setCropName(request.cropName().trim());
        product.setQuantity(request.quantity());
        product.setUnitPrice(request.unitPrice());
        product.setHarvestDate(request.harvestDate());
        product.setLocation(request.location().trim());
        product.setActive(true);
        return toResponse(productRepository.save(product));
    }

    @Transactional
    public ProductResponse updateProduct(Long id, UUID farmerId, UserRole role, ProductUpdateRequest request) {
        Product product = findProduct(id);
        if (role != UserRole.ADMIN && !product.getFarmerId().equals(farmerId)) {
            throw ApiException.forbidden("You may only update your own listings");
        }

        int previousQuantity = product.getQuantity();

        if (request.unitPrice() != null) {
            product.setUnitPrice(request.unitPrice());
        }
        if (request.quantity() != null) {
            product.setQuantity(request.quantity());
        }
        if (request.active() != null) {
            product.setActive(request.active());
        }

        Product saved = productRepository.save(product);
        if (request.quantity() != null && !request.quantity().equals(previousQuantity)) {
            stockEventService.evaluateQuantityChange(saved, previousQuantity, saved.getQuantity());
        }
        return toResponse(saved);
    }

    private Product findProduct(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> ApiException.notFound("Product not found: " + id));
    }

    private ProductResponse toResponse(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getFarmerId(),
                product.getCropName(),
                product.getQuantity(),
                product.getUnitPrice(),
                product.getHarvestDate(),
                product.getLocation(),
                product.isActive());
    }
}