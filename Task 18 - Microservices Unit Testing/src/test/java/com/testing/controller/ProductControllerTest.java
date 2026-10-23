package com.testing.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.testing.exception.ProductNotFoundException;
import com.testing.model.Product;
import com.testing.service.ProductService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductController.class)
@DisplayName("Product Controller Slice Tests (REST Endpoints & HTTP Protocols)")
public class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("GET /products should return 200 OK and list of products")
    void testGetAllProducts_ReturnsOk() throws Exception {
        Product p1 = new Product(1L, "4K Monitor", "IPS UltraHD", 25000.0, 5);
        Product p2 = new Product(2L, "Mechanical Keyboard", "RGB switches", 4500.0, 15);

        when(productService.getAllProducts()).thenReturn(Arrays.asList(p1, p2));

        mockMvc.perform(get("/products")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", is("4K Monitor")))
                .andExpect(jsonPath("$[1].name", is("Mechanical Keyboard")));

        verify(productService, times(1)).getAllProducts();
    }

    @Test
    @DisplayName("GET /products/{id} should return 200 OK when found")
    void testGetProductById_Success() throws Exception {
        Product product = new Product(1L, "USB-C Hub", "Multiport 7-in-1", 2500.0, 30);
        when(productService.getProductById(1L)).thenReturn(product);

        mockMvc.perform(get("/products/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("USB-C Hub")))
                .andExpect(jsonPath("$.price", is(2500.0)));

        verify(productService, times(1)).getProductById(1L);
    }

    @Test
    @DisplayName("POST /products with valid payload should return 201 Created")
    void testAddProduct_ValidPayload_ReturnsCreated() throws Exception {
        Product newProduct = new Product(null, "Noise Cancelling Headphones", "Wireless ANC", 12000.0, 8);
        Product savedProduct = new Product(10L, "Noise Cancelling Headphones", "Wireless ANC", 12000.0, 8);

        when(productService.addProduct(any(Product.class))).thenReturn(savedProduct);

        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newProduct)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(10)))
                .andExpect(jsonPath("$.name", is("Noise Cancelling Headphones")));

        verify(productService, times(1)).addProduct(any(Product.class));
    }

    @Test
    @DisplayName("POST /products with invalid payload should fail Bean validation with 400 Bad Request")
    void testAddProduct_InvalidPayload_ReturnsBadRequest() throws Exception {
        // Blank name and negative price
        Product invalidProduct = new Product(null, "", "", -500.0, -2);

        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidProduct)))
                .andExpect(status().isBadRequest());

        verify(productService, never()).addProduct(any(Product.class));
    }

    @Test
    @DisplayName("PUT /products/{id} should return 200 OK on update")
    void testUpdateProduct_Success() throws Exception {
        Product updatePayload = new Product(1L, "USB-C Hub Pro", "Upgraded 10-in-1", 3200.0, 20);
        when(productService.updateProduct(eq(1L), any(Product.class))).thenReturn(updatePayload);

        mockMvc.perform(put("/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatePayload)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("USB-C Hub Pro")))
                .andExpect(jsonPath("$.price", is(3200.0)));

        verify(productService, times(1)).updateProduct(eq(1L), any(Product.class));
    }

    @Test
    @DisplayName("DELETE /products/{id} should return 204 No Content")
    void testDeleteProduct_Success() throws Exception {
        doNothing().when(productService).deleteProduct(1L);

        mockMvc.perform(delete("/products/1"))
                .andExpect(status().isNoContent());

        verify(productService, times(1)).deleteProduct(1L);
    }
}
