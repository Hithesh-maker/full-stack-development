package com.orderservice.client;

import com.orderservice.dto.ProductDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class ProductClientFallback implements ProductClient {

    private static final Logger logger = LoggerFactory.getLogger(ProductClientFallback.class);

    @Override
    public List<ProductDto> getAllProducts() {
        logger.warn("[Resilience Fallback Triggered] Product Service is unreachable for getAllProducts(). Serving fallback catalog.");
        ProductDto fallback = new ProductDto(
                0L,
                "Cached/Fallback Catalog",
                "Downstream Product Service temporarily offline. Fallback cached catalog item.",
                0.0,
                0
        );
        return Collections.singletonList(fallback);
    }

    @Override
    public ProductDto getProductById(Long id) {
        logger.warn("[Resilience Fallback Triggered] Product Service is unreachable for getProductById({}). Serving fallback response.", id);
        return new ProductDto(
                id,
                "Fallback Product #" + id,
                "Product details unavailable due to downstream service outage. Graceful fallback executed.",
                0.0,
                0
        );
    }
}
