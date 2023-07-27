package com.claravalstore.backend.repositories;

import com.claravalstore.backend.entities.Address;
import com.claravalstore.backend.tests.Factory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class AddressRepositoryTests {

    @Autowired
    private AddressRepository repository;

    private long existingId;
    private long nonExistingId;
    private long countTotalAddress;

    @BeforeEach
    void setUp() {
        existingId = 1L;
        nonExistingId = 1000L;
        countTotalAddress = 5L;
    }

    @Test
    void saveShouldPersistWithAutoIncrementWhenIdIsNull() {
        Address address = Factory.createAddress();
        address.setId(null);

        address = repository.save(address);

        Assertions.assertNotNull(address.getId());
        Assertions.assertEquals(countTotalAddress + 1, address.getId());
    }

    @Test
    void searchAddressByLoggedInUserShouldReturnAddressWhenUserExists() {
        Address result = repository.searchAddressByLoggedInUser(existingId);
        Assertions.assertNotNull(result);
    }

    @Test
    void searchAddressByLoggedInUserShouldReturnNullWhenUserDoesNotExist() {
        Address result = repository.searchAddressByLoggedInUser(nonExistingId);
        Assertions.assertNull(result);
    }
}
