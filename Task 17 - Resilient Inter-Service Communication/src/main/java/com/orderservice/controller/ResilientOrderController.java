package com.orderservice.controller;

import com.orderservice.client.ProductClient;
import com.orderservice.dto.OrderResponseDto;
import com.orderservice.dto.ProductDto;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/resilient-orders")
public class ResilientOrderController {

    private static final Logger logger = LoggerFactory.getLogger(ResilientOrderController.class);
    private final ProductClient productClient;

    public ResilientOrderController(ProductClient productClient) {
        this.productClient = productClient;
    }

    /**
     * Consumes Product Service REST endpoint with Circuit Breaker protection.
     */
    @GetMapping("/catalog")
    @CircuitBreaker(name = "productService", fallbackMethod = "getCatalogFallback")
    public ResponseEntity<List<ProductDto>> getLiveCatalog() {
        logger.info("[Order Service] Calling downstream Product Service for catalog list...");
        List<ProductDto> products = productClient.getAllProducts();
        return ResponseEntity.ok(products);
    }

    public ResponseEntity<List<ProductDto>> getCatalogFallback(Throwable throwable) {
        logger.error("[CircuitBreaker Active] Downstream Product Service failure: {}", throwable.getMessage());
        ProductDto fallback = new ProductDto(
                -1L,
                "Emergency Fallback Catalog",
                "Product Service is currently unavailable. Circuit breaker fallback activated.",
                0.0,
                0
        );
        return ResponseEntity.ok(List.of(fallback));
    }

    /**
     * Generates an enriched order response combining order logic with downstream product details.
     */
    @GetMapping("/{id}")
    @CircuitBreaker(name = "productService", fallbackMethod = "getOrderDetailsFallback")
    public ResponseEntity<OrderResponseDto> getOrderWithProductDetails(@PathVariable Long id) {
        logger.info("[Order Service] Fetching Order #{} and resolving Product details from Product Service...", id);

        Long simulatedProductId = 1L;
        ProductDto product = productClient.getProductById(simulatedProductId);

        boolean isFallback = product.getId() <= 0L || product.getName().contains("Fallback");

        OrderResponseDto response = new OrderResponseDto(
                id,
                simulatedProductId,
                "Hithesh Enterprise Customer",
                2,
                (product.getPrice() != null ? product.getPrice() : 0.0) * 2,
                "CONFIRMED",
                isFallback ? "DEGRADED_SERVICE_FALLBACK" : "LIVE_INTEGRATED_SERVICE",
                isFallback
                        ? "Product Service is down. Returned cached/fallback product information without crashing."
                        : "Successfully retrieved live product details from Product Service.",
                product
        );

        return ResponseEntity.ok(response);
    }

    public ResponseEntity<OrderResponseDto> getOrderDetailsFallback(Long id, Throwable throwable) {
        logger.warn("[CircuitBreaker Triggered] getOrderDetailsFallback for Order #{}: {}", id, throwable.getMessage());
        ProductDto fallbackProduct = new ProductDto(
                1L,
                "Fallback Product Placeholder",
                "Downstream Product Service unreachable.",
                0.0,
                0
        );

        OrderResponseDto degradedResponse = new OrderResponseDto(
                id,
                1L,
                "Hithesh Customer (Degraded Mode)",
                1,
                0.0,
                "PENDING_CATALOG_SYNC",
                "DEGRADED_SERVICE_FALLBACK",
                "Downstream failure intercepted gracefully by Resilience4j: " + throwable.getMessage(),
                fallbackProduct
        );

        return ResponseEntity.ok(degradedResponse);
    }
}
