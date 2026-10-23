package com.testing.repository;

import com.testing.model.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@DisplayName("Product Repository Data Layer Tests (Isolated Persistence with H2)")
public class ProductRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ProductRepository productRepository;

    @Test
    @DisplayName("Should persist product and retrieve by ID")
    void testSaveAndFindById_Success() {
        Product product = new Product("Ergonomic Chair", "High back mesh office chair", 14500.0, 12);
        Product saved = entityManager.persistFlushFind(product);

        Optional<Product> found = productRepository.findById(saved.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Ergonomic Chair");
        assertThat(found.get().getPrice()).isEqualTo(14500.0);
    }

    @Test
    @DisplayName("Should find products by partial name match case-insensitively")
    void testFindByNameContainingIgnoreCase() {
        entityManager.persist(new Product("Apple MacBook Pro", "M3 Max chip", 249000.0, 5));
        entityManager.persist(new Product("Dell XPS 15", "Intel Core Ultra", 185000.0, 7));
        entityManager.persist(new Product("Apple iPad Air", "M2 chip tablet", 59000.0, 15));
        entityManager.flush();

        List<Product> appleProducts = productRepository.findByNameContainingIgnoreCase("apple");

        assertThat(appleProducts).hasSize(2);
        assertThat(appleProducts).extracting(Product::getName)
                .containsExactlyInAnyOrder("Apple MacBook Pro", "Apple iPad Air");
    }

    @Test
    @DisplayName("Should check existence by exact name")
    void testExistsByName() {
        entityManager.persist(new Product("Mechanical Keyboard", "Cherry MX Blue switches", 6000.0, 10));
        entityManager.flush();

        boolean exists = productRepository.existsByName("Mechanical Keyboard");
        boolean notExists = productRepository.existsByName("Nonexistent Gizmo");

        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
    }

    @Test
    @DisplayName("Should delete product successfully from database")
    void testDeleteById() {
        Product product = entityManager.persistFlushFind(new Product("Temporary Item", "To be deleted", 100.0, 1));

        productRepository.deleteById(product.getId());

        Product found = entityManager.find(Product.class, product.getId());
        assertThat(found).isNull();
    }
}
