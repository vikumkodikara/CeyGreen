package com.ceygreen.ecommerce.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ceygreen.ecommerce.common.ApiException;
import com.ceygreen.ecommerce.config.MarketplaceProperties;
import com.ceygreen.ecommerce.dto.ProductCreateRequest;
import com.ceygreen.ecommerce.dto.ProductUpdateRequest;
import com.ceygreen.ecommerce.entity.Product;
import com.ceygreen.ecommerce.repository.ProductRepository;
import com.ceygreen.ecommerce.security.UserRole;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ProductServiceTest {

    private static final UUID FARMER_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private static final UUID OTHER_FARMER = UUID.fromString("99999999-9999-9999-9999-999999999999");

    @Mock
    private ProductRepository productRepository;

    @Mock
    private StockEventService stockEventService;

    @Mock
    private MarketplaceProperties marketplaceProperties;

    @InjectMocks
    private ProductService productService;

    @BeforeEach
    void setUp() {
        MarketplaceProperties.Stock stock = new MarketplaceProperties.Stock();
        stock.setLowThreshold(10);
        when(marketplaceProperties.getStock()).thenReturn(stock);
    }

    @Test
    void createProductPersistsSpecFields() {
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> {
            Product saved = invocation.getArgument(0);
            saved.setId(1L);
            return saved;
        });

        var response = productService.createProduct(
                FARMER_ID,
                new ProductCreateRequest(
                        "Tomato", 20, new BigDecimal("150.00"), LocalDate.of(2026, 8, 1), "Kandy", null, null));

        assertThat(response.cropName()).isEqualTo("Tomato");
        assertThat(response.farmerId()).isEqualTo(FARMER_ID);
        assertThat(response.active()).isTrue();

        ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);
        verify(productRepository).save(captor.capture());
        assertThat(captor.getValue().getUnitPrice()).isEqualByComparingTo("150.00");
    }

    @Test
    void listProductsFiltersByCropAndLocation() {
        Product product = sampleProduct();
        when(productRepository.findAll(any(Specification.class), any(PageRequest.class)))
                .thenReturn(new PageImpl<>(List.of(product)));

        var results = productService.listProducts(
                null, "Tomato", "Kandy", null, null, null, null, null, null, null, PageRequest.of(0, 20));

        assertThat(results.getContent()).hasSize(1);
        assertThat(results.getContent().get(0).cropName()).isEqualTo("Tomato");
    }

    @Test
    void getProductNotFoundThrows404() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.getProduct(99L))
                .isInstanceOf(ApiException.class)
                .extracting(ex -> ((ApiException) ex).getStatus().value())
                .isEqualTo(404);
    }

    @Test
    void farmerCannotUpdateAnotherFarmersProduct() {
        Product product = sampleProduct();
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        assertThatThrownBy(() -> productService.updateProduct(
                        1L, OTHER_FARMER, UserRole.FARMER, new ProductUpdateRequest(null, null, false, null, null, null)))
                .isInstanceOf(ApiException.class)
                .extracting(ex -> ((ApiException) ex).getStatus().value())
                .isEqualTo(403);
    }

    @Test
    void farmerCanUpdateOwnProduct() {
        Product product = sampleProduct();
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.save(product)).thenReturn(product);

        var response = productService.updateProduct(
                1L, FARMER_ID, UserRole.FARMER, new ProductUpdateRequest(null, null, false, null, null, null));

        assertThat(response.active()).isFalse();
    }

    private static Product sampleProduct() {
        Product product = new Product();
        product.setId(1L);
        product.setFarmerId(FARMER_ID);
        product.setCropName("Tomato");
        product.setQuantity(10);
        product.setUnitPrice(new BigDecimal("100.00"));
        product.setHarvestDate(LocalDate.of(2026, 8, 1));
        product.setLocation("Kandy");
        product.setActive(true);
        return product;
    }
}
