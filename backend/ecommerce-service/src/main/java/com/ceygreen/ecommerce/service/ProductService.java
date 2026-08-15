package com.ceygreen.ecommerce.service;

import com.ceygreen.ecommerce.common.ApiException;
import com.ceygreen.ecommerce.config.MarketplaceProperties;
import com.ceygreen.ecommerce.dto.ProductCreateRequest;
import com.ceygreen.ecommerce.dto.ProductResponse;
import com.ceygreen.ecommerce.dto.ProductStatusUpdateRequest;
import com.ceygreen.ecommerce.dto.ProductUpdateRequest;
import com.ceygreen.ecommerce.dto.StockUpdateRequest;
import com.ceygreen.ecommerce.entity.Product;
import com.ceygreen.ecommerce.entity.ProductListingStatus;
import com.ceygreen.ecommerce.repository.ProductRepository;
import com.ceygreen.ecommerce.repository.ProductSpecifications;
import com.ceygreen.ecommerce.security.UserRole;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final StockEventService stockEventService;
    private final MarketplaceProperties marketplaceProperties;

    public ProductService(
            ProductRepository productRepository,
            StockEventService stockEventService,
            MarketplaceProperties marketplaceProperties) {
        this.productRepository = productRepository;
        this.stockEventService = stockEventService;
        this.marketplaceProperties = marketplaceProperties;
    }

    public Page<ProductResponse> listProducts(
            String q,
            String cropName,
            String location,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            Boolean inStock,
            Boolean active,
            UUID farmerId,
            ProductListingStatus statusFilter,
            String sort,
            Pageable pageable) {
        Pageable sorted = applySort(pageable, sort);
        return productRepository
                .findAll(ProductSpecifications.withFilters(
                        q, cropName, location, minPrice, maxPrice, inStock, active, farmerId, statusFilter), sorted)
                .map(ProductMapper::toResponse);
    }

    public List<String> listCategories() {
        return productRepository.findDistinctActiveCropNames();
    }

    public List<ProductResponse> listFeatured(int limit) {
        int size = limit > 0 ? Math.min(limit, 50) : 8;
        return productRepository.findFeatured(PageRequest.of(0, size)).stream()
                .map(ProductMapper::toResponse)
                .toList();
    }

    public List<ProductResponse> listLowStock(UUID farmerId) {
        int threshold = marketplaceProperties.getStock().getLowThreshold();
        return productRepository.findByFarmerIdAndQuantityLessThanEqualAndActiveTrue(farmerId, threshold).stream()
                .map(ProductMapper::toResponse)
                .toList();
    }

    public Page<ProductResponse> listFarmerProducts(
            UUID farmerId,
            ProductListingStatus statusFilter,
            String sort,
            Pageable pageable) {
        return listProducts(null, null, null, null, null, null, null, farmerId, statusFilter, sort, pageable);
    }

    public ProductResponse getProduct(Long id) {
        return ProductMapper.toResponse(findProduct(id));
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
        product.setDescription(request.description());
        product.setImageUrl(request.imageUrl());
        product.setActive(true);
        return ProductMapper.toResponse(productRepository.save(product));
    }

    @Transactional
    public ProductResponse updateProduct(Long id, UUID farmerId, UserRole role, ProductUpdateRequest request) {
        Product product = findProduct(id);
        assertOwnership(product, farmerId, role);

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
        if (request.description() != null) {
            product.setDescription(request.description());
        }
        if (request.imageUrl() != null) {
            product.setImageUrl(request.imageUrl());
        }
        if (request.location() != null && !request.location().isBlank()) {
            product.setLocation(request.location().trim());
        }

        Product saved = productRepository.save(product);
        if (request.quantity() != null && !request.quantity().equals(previousQuantity)) {
            stockEventService.evaluateQuantityChange(saved, previousQuantity, saved.getQuantity());
        }
        return ProductMapper.toResponse(saved);
    }

    @Transactional
    public void deleteProduct(Long id, UUID farmerId, UserRole role) {
        Product product = findProduct(id);
        assertOwnership(product, farmerId, role);
        product.setActive(false);
        productRepository.save(product);
    }

    @Transactional
    public ProductResponse updateStock(Long id, UUID farmerId, UserRole role, StockUpdateRequest request) {
        Product product = findProduct(id);
        assertOwnership(product, farmerId, role);
        int previousQuantity = product.getQuantity();
        product.setQuantity(request.quantity());
        Product saved = productRepository.save(product);
        stockEventService.evaluateQuantityChange(saved, previousQuantity, saved.getQuantity());
        return ProductMapper.toResponse(saved);
    }

    @Transactional
    public ProductResponse updateStatus(Long id, UUID farmerId, UserRole role, ProductStatusUpdateRequest request) {
        Product product = findProduct(id);
        assertOwnership(product, farmerId, role);
        product.setActive(request.active());
        return ProductMapper.toResponse(productRepository.save(product));
    }

    public long countByFarmer(UUID farmerId) {
        return productRepository.findByFarmerId(farmerId).size();
    }

    public long countActiveByFarmer(UUID farmerId) {
        return productRepository.findByFarmerId(farmerId).stream().filter(Product::isActive).count();
    }

    public long countInactiveByFarmer(UUID farmerId) {
        return productRepository.findByFarmerId(farmerId).stream().filter(p -> !p.isActive()).count();
    }

    public long countLowStockByFarmer(UUID farmerId) {
        int threshold = marketplaceProperties.getStock().getLowThreshold();
        return productRepository.findByFarmerIdAndQuantityLessThanEqualAndActiveTrue(farmerId, threshold).size();
    }

    private Product findProduct(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> ApiException.notFound("Product not found: " + id));
    }

    private static void assertOwnership(Product product, UUID farmerId, UserRole role) {
        if (role != UserRole.ADMIN && !product.getFarmerId().equals(farmerId)) {
            throw ApiException.forbidden("You may only manage your own listings");
        }
    }

    private static Pageable applySort(Pageable pageable, String sort) {
        if (sort == null || sort.isBlank()) {
            return pageable;
        }
        Sort order = switch (sort) {
            case "price_asc" -> Sort.by(Sort.Direction.ASC, "unitPrice");
            case "price_desc" -> Sort.by(Sort.Direction.DESC, "unitPrice");
            case "newest" -> Sort.by(Sort.Direction.DESC, "createdAt");
            default -> Sort.unsorted();
        };
        return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), order);
    }
}
