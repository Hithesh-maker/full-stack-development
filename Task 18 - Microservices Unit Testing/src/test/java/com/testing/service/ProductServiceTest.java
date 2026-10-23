package com.testing.service;

import com.testing.exception.ProductNotFoundException;
import com.testing.model.Product;
import com.testing.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Product Service Unit Tests (Isolated Service Logic)")
public class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    private Product sampleProduct;

    @BeforeEach
    void setUp() {
        sampleProduct = new Product(1L, "Developer Laptop", "High-performance workstation", 75000.0, 10);
    }

    @Test
    @DisplayName("Should retrieve all products successfully")
    void testGetAllProducts_Success() {
        Product secondProduct = new Product(2L, "Wireless Mouse", "Ergonomic Bluetooth Mouse", 1500.0, 25);
        when(productRepository.findAll()).thenReturn(Arrays.asList(sampleProduct, secondProduct));

        List<Product> products = productService.getAllProducts();

        assertThat(products).isNotNull();
        assertThat(products.size()).isEqualTo(2);
        assertThat(products.get(0).getName()).isEqualTo("Developer Laptop");
        verify(productRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should find product by existing ID")
    void testGetProductById_Success() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(sampleProduct));

        Product found = productService.getProductById(1L);

        assertThat(found).isNotNull();
        assertThat(found.getId()).isEqualTo(1L);
        assertThat(found.getPrice()).isEqualTo(75000.0);
        verify(productRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should throw ProductNotFoundException when ID does not exist")
    void testGetProductById_NotFound_ThrowsException() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () -> {
            productService.getProductById(99L);
        });

        verify(productRepository, times(1)).findById(99L);
    }

    @Test
    @DisplayName("Should save and return created product")
    void testAddProduct_Success() {
        when(productRepository.save(any(Product.class))).thenReturn(sampleProduct);

        Product created = productService.addProduct(sampleProduct);

        assertThat(created).isNotNull();
        assertThat(created.getName()).isEqualTo("Developer Laptop");
        verify(productRepository, times(1)).save(sampleProduct);
    }

    @Test
    @DisplayName("Should reject product addition with zero or negative price")
    void testAddProduct_InvalidPrice_ThrowsException() {
        Product invalidProduct = new Product(3L, "Broken Item", "Invalid", -50.0, 5);

        assertThrows(IllegalArgumentException.class, () -> {
            productService.addProduct(invalidProduct);
        });

        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    @DisplayName("Should update existing product attributes")
    void testUpdateProduct_Success() {
        Product updateInfo = new Product(1L, "Developer Laptop Pro", "Updated specs with 64GB RAM", 85000.0, 8);
        when(productRepository.findById(1L)).thenReturn(Optional.of(sampleProduct));
        when(productRepository.save(any(Product.class))).thenReturn(updateInfo);

        Product updated = productService.updateProduct(1L, updateInfo);

        assertThat(updated.getName()).isEqualTo("Developer Laptop Pro");
        assertThat(updated.getPrice()).isEqualTo(85000.0);
        verify(productRepository, times(1)).save(sampleProduct);
    }

    @Test
    @DisplayName("Should delete product when ID exists")
    void testDeleteProduct_Success() {
        when(productRepository.existsById(1L)).thenReturn(true);
        doNothing().when(productRepository).deleteById(1L);

        productService.deleteProduct(1L);

        verify(productRepository, times(1)).existsById(1L);
        verify(productRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Should throw exception when attempting to delete non-existent product")
    void testDeleteProduct_NotFound_ThrowsException() {
        when(productRepository.existsById(404L)).thenReturn(false);

        assertThrows(ProductNotFoundException.class, () -> {
            productService.deleteProduct(404L);
        });

        verify(productRepository, never()).deleteById(anyLong());
    }
}
