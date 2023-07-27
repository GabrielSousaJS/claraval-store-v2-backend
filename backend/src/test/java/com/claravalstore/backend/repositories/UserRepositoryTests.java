package com.claravalstore.backend.repositories;

import com.claravalstore.backend.entities.User;
import com.claravalstore.backend.tests.Factory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

@DataJpaTest
class UserRepositoryTests {

    @Autowired
    private UserRepository repository;

    private long existingId;
    private long nonExistingId;
    private long countTotalUsers;
    private String existingName;
    private String nonExistingName;

    @BeforeEach
    void setUp() {
        existingId = 1L;
        nonExistingId = 1000L;
        countTotalUsers = 5L;
        existingName = "Gabriela Oliveira";
        nonExistingName = "João Silva";
    }

    @Test
    void findByIdShouldReturnOptionalWhenExistId() {
        Optional<User> result = repository.findById(existingId);
        Assertions.assertTrue(result.isPresent());
    }

    @Test
    void findByIdShouldReturnEmptyWhenIdDoesNotExist() {
        Optional<User> result = repository.findById(nonExistingId);
        Assertions.assertFalse(result.isPresent());
    }

    @Test
    void findAllShouldReturnPageUser() {
        Pageable pageable = PageRequest.of(0, 12);

        Page<User> page = repository.findAll(pageable);

        Assertions.assertFalse(page.isEmpty());
        Assertions.assertEquals(0, page.getNumber());
        Assertions.assertEquals(12, page.getSize());
    }

    @Test
    void findByNameShouldReturnUserWhenExistName() {
        User result = repository.findByName(existingName);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(existingName, result.getName());
    }

    @Test
    void findByNameShouldReturnNullWhenNameDoesNotExist() {
        User result = repository.findByName(nonExistingName);
        Assertions.assertNull(result);
    }

    @Test
    void saveShouldPersistWithAutoincrementWhenIdIsNull() {
        User user = Factory.createUser();
        user.setId(null);

        user = repository.save(user);

        Assertions.assertNotNull(user.getId());
        Assertions.assertNotNull(user.getPrivileges());
        Assertions.assertEquals(countTotalUsers + 1, user.getId());
    }
}
