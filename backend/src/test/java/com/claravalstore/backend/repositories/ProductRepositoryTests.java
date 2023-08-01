package com.claravalstore.backend.repositories;

import com.claravalstore.backend.dto.ProductDTO;
import com.claravalstore.backend.entities.Product;
import com.claravalstore.backend.projections.ProductProjection;
import com.claravalstore.backend.tests.Factory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

@DataJpaTest
class ProductRepositoryTests {

    @Autowired
    private ProductRepository repository;

    private long existingId;
    private long nonExistingId;
    private long countTotalProducts;
    private List<Long> categoryIds;

    @BeforeEach
    void setUp() {
        existingId = 1L;
        nonExistingId = 1000L;
        countTotalProducts = 48L;

        categoryIds = List.of();
    }

    @Test
    void findAllShouldReturnPageProduct() {
        Pageable pageable = PageRequest.of(0, 12);

        Page<ProductProjection> pageProjection = repository.searchProducts(categoryIds, "", pageable);
        List<Long> productIds = pageProjection.map(ProductProjection::getId).toList();
        List<Product> list = repository.searchProductsWithCategories(productIds);

        Assertions.assertFalse(list.isEmpty());
        Assertions.assertEquals(pageProjection.getTotalElements(), countTotalProducts);
    }

    @Test
    void searchByIdShouldReturnOptionalWhenExistId() {
        Optional<Product> result = repository.searchById(existingId);
        Assertions.assertTrue(result.isPresent());
    }

    @Test
    void searchByIdShouldReturnEmptyWhenIdDoesNotExist() {
        Optional<Product> result = repository.searchById(nonExistingId);
        Assertions.assertFalse(result.isPresent());
    }

    @Test
    void saveShouldPersistWithAutoincrementWhenIdIsNull() {
        Product product = Factory.createProduct();
        product.setId(null);

        product = repository.save(product);

        Assertions.assertNotNull(product.getId());
        Assertions.assertEquals(countTotalProducts + 1, product.getId());
    }

    @Test
    void deleteShouldDeleteObjectWhenIdExists() {
        repository.deleteById(existingId);

        Optional<Product> result = repository.findById(existingId);
        Assertions.assertFalse(result.isPresent());
    }
}
