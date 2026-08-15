package com.ceygreen.ecommerce;

import static org.assertj.core.api.Assertions.assertThat;

import com.ceygreen.ecommerce.common.ApiException;
import com.ceygreen.ecommerce.dto.CheckoutRequest;
import com.ceygreen.ecommerce.entity.Product;
import com.ceygreen.ecommerce.repository.ProductRepository;
import com.ceygreen.ecommerce.service.OrderService;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;

@SpringBootTest
@EmbeddedKafka(partitions = 1, topics = {"order-events", "stock-events"})
class CheckoutConcurrencyTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private ProductRepository productRepository;

    @Test
    void concurrentCheckoutDoesNotOversellStock() throws Exception {
        Product product = new Product();
        product.setFarmerId(UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa"));
        product.setCropName("ConcurrencyCrop");
        product.setQuantity(5);
        product.setUnitPrice(new BigDecimal("100.00"));
        product.setHarvestDate(LocalDate.of(2026, 8, 9));
        product.setLocation("Colombo");
        product.setActive(true);
        Long productId = productRepository.save(product).getId();

        ExecutorService pool = Executors.newFixedThreadPool(2);
        CountDownLatch ready = new CountDownLatch(2);
        CountDownLatch start = new CountDownLatch(1);
        AtomicInteger successes = new AtomicInteger();
        AtomicInteger failures = new AtomicInteger();

        for (int i = 0; i < 2; i++) {
            UUID buyerId = UUID.fromString(String.format("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbb%d", i));
            pool.submit(() -> {
                ready.countDown();
                try {
                    start.await();
                    orderService.checkout(buyerId, new CheckoutRequest(
                            null,
                            productId,
                            4,
                            "Buyer",
                            "0770000000",
                            "1 Test Road",
                            "Colombo",
                            "00100"));
                    successes.incrementAndGet();
                } catch (ApiException ex) {
                    failures.incrementAndGet();
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
            });
        }

        ready.await(5, TimeUnit.SECONDS);
        start.countDown();
        pool.shutdown();
        assertThat(pool.awaitTermination(30, TimeUnit.SECONDS)).isTrue();

        assertThat(successes.get()).isEqualTo(1);
        assertThat(failures.get()).isEqualTo(1);

        Product updated = productRepository.findById(productId).orElseThrow();
        assertThat(updated.getQuantity()).isEqualTo(1);
    }
}