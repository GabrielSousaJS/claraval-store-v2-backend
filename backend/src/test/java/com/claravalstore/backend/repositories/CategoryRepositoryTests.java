package com.claravalstore.backend.repositories;

import com.claravalstore.backend.entities.Category;
import com.claravalstore.backend.tests.Factory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

@DataJpaTest
class CategoryRepositoryTests {

    @Autowired
    private CategoryRepository repository;

    private long existingId;
    private long nonExistingId;
    private long countTotalCategories;

    @BeforeEach
    void setUp() {
        existingId = 1L;
        nonExistingId = 1000L;
        countTotalCategories = 6L;
    }

    @Test
    void findByIdShouldReturnOptionalWhenExistId() {
        Optional<Category> result = repository.findById(existingId);
        Assertions.assertTrue(result.isPresent());
    }

    @Test
    void findByIdShouldReturnEmptyWhenIdDoesNotExist() {
        Optional<Category> result = repository.findById(nonExistingId);
        Assertions.assertFalse(result.isPresent());
    }

    @Test
    void saveShouldPersistWithAutoincrementWhenIdIsNull() {
        Category category = Factory.createCategory();
        category.setId(null);

        category = repository.save(category);

        Assertions.assertNotNull(category.getId());
        Assertions.assertEquals(countTotalCategories + 1, category.getId());
    }

    @Test
    void deleteShouldDeleteObjectWhenIdExists() {
        repository.deleteById(existingId);
        Optional<Category> result = repository.findById(existingId);

        Assertions.assertFalse(result.isPresent());
    }
}
