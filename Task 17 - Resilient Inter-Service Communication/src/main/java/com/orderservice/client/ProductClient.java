package com.orderservice.client;

import com.orderservice.dto.ProductDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "product-service", fallback = ProductClientFallback.class)
public interface ProductClient {

    @GetMapping("/products")
    List<ProductDto> getAllProducts();

    @GetMapping("/products/{id}")
    ProductDto getProductById(@PathVariable("id") Long id);
}
