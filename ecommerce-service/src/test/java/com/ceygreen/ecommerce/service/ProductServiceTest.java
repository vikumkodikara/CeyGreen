package com.ceygreen.ecommerce.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ceygreen.ecommerce.common.ApiException;
import com.ceygreen.ecommerce.dto.ProductCreateRequest;
import com.ceygreen.ecommerce.dto.ProductUpdateRequest;
import com.ceygreen.ecommerce.entity.Product;
import com.ceygreen.ecommerce.repository.ProductRepository;
import com.ceygreen.ecommerce.service.StockEventService;
import com.ceygreen.ecommerce.security.UserRole;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    private static final UUID FARMER_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private static final UUID OTHER_FARMER = UUID.fromString("99999999-9999-9999-9999-999999999999");

    @Mock
    private ProductRepository productRepository;

    @Mock
    private StockEventService stockEventService;

    @InjectMocks
    private ProductService productService;

    @Test
    void createProductPersistsSpecFields() {
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> {
            Product saved = invocation.getArgument(0);
            saved.setId(1L);
            return saved;
        });

        var response = productService.createProduct(
                FARMER_ID,
                new ProductCreateRequest("Tomato", 20, new BigDecimal("150.00"), LocalDate.of(2026, 8, 1), "Kandy"));

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
        when(productRepository.findByActiveTrueAndCropNameIgnoreCaseAndLocationIgnoreCase("Tomato", "Kandy"))
                .thenReturn(List.of(product));

        var results = productService.listProducts("Tomato", "Kandy");

        assertThat(results).hasSize(1);
        assertThat(results.get(0).cropName()).isEqualTo("Tomato");
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
                        1L, OTHER_FARMER, UserRole.FARMER, new ProductUpdateRequest(null, null, false)))
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
                1L, FARMER_ID, UserRole.FARMER, new ProductUpdateRequest(null, null, false));

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