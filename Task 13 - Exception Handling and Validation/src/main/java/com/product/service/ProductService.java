package com.product.service;

import com.product.Product;
import com.product.exception.ProductNotFoundException;
import com.product.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    // GET all products
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    // GET product by ID
    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    // POST - Add product
    public Product addProduct(Product product) {
        return productRepository.save(product);
    }

    // PUT - Update product
    public Product updateProduct(Long id, Product product) {

        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));

        existingProduct.setName(product.getName());
        existingProduct.setDescription(product.getDescription());
        existingProduct.setPrice(product.getPrice());
        existingProduct.setQuantity(product.getQuantity());

        return productRepository.save(existingProduct);
    }

    // DELETE - Delete product
    public void deleteProduct(Long id) {

        if (!productRepository.existsById(id)) {
            throw new ProductNotFoundException(id);
        }

        productRepository.deleteById(id);
    }
}