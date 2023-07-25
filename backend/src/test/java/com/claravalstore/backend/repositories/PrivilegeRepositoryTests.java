package com.claravalstore.backend.repositories;

import com.claravalstore.backend.entities.Privilege;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class PrivilegeRepositoryTests {

    @Autowired
    private PrivilegeRepository repository;

    private String existingPrivilege;
    private String nonExistingPrivilege;

    @BeforeEach
    void setUp() {
        existingPrivilege = "ROLE_ADMIN";
        nonExistingPrivilege = "ROLE_UNDEFINED";
    }

    @Test
    void findPrivilegeByAuthorityShouldReturnPrivilegeWhenPrivilegeExists() {
        Privilege result = repository.findPrivilegeByAuthority(existingPrivilege);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(existingPrivilege, result.getAuthority());
    }

    @Test
    void findPrivilegeByAuthoriryShouldReturnNullWhenPrivilegeDoesNotExist() {
        Privilege result = repository.findPrivilegeByAuthority(nonExistingPrivilege);
        Assertions.assertNull(result);
    }
}
