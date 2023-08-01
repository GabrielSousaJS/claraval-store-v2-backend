package com.claravalstore.backend.services;

import com.claravalstore.backend.dto.UserDTO;
import com.claravalstore.backend.dto.UserMinDTO;
import com.claravalstore.backend.entities.Address;
import com.claravalstore.backend.entities.User;
import com.claravalstore.backend.repositories.AddressRepository;
import com.claravalstore.backend.repositories.UserRepository;
import com.claravalstore.backend.services.exceptions.ResourceNotFoundException;
import com.claravalstore.backend.tests.Factory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

@ExtendWith(SpringExtension.class)
class UserServiceTests {

    @InjectMocks
    private UserService service;

    @Mock
    private UserRepository repository;

    @Mock
    private AddressService addressService;

    @Mock
    private PrivilegeService privilegeService;

    @Mock
    private PasswordEncoder passwordEncoder;

    private long existingId;
    private long nonExistingId;
    private User user;
    PageImpl<User> page;

    @BeforeEach
    void setUp() {
        existingId = 1L;
        nonExistingId = 2L;
        user = Factory.createUser();
        page = new PageImpl<>(List.of(user));

        Mockito.when(repository.findAll((Pageable) ArgumentMatchers.any())).thenReturn(page);

        Mockito.when(repository.findById(existingId)).thenReturn(Optional.of(user));
        Mockito.when(repository.findById(nonExistingId)).thenReturn(Optional.empty());

        Mockito.when(repository.save(ArgumentMatchers.any())).thenReturn(user);

        Mockito.when(addressService.insert(ArgumentMatchers.any())).thenReturn(Factory.createAddress());

        Mockito.when(privilegeService.clientPrivilege()).thenReturn(Factory.createPrivilege());

        Mockito.when(passwordEncoder.encode(ArgumentMatchers.any())).thenReturn(user.getPassword());
    }

    @Test
    void findAllPagedReturnPage() {
        Pageable pageable = PageRequest.of(0, 12);

        Page<UserMinDTO> result = service.findAllPaged(pageable);

        Assertions.assertNotNull(result);
        Mockito.verify(repository).findAll(pageable);
    }

    @Test
    void findByIdShouldReturnUserDTOWhenIdExists() {
        UserDTO result = service.findById(existingId);

        Assertions.assertNotNull(result);
        Mockito.verify(repository).findById(existingId);
    }

    @Test
    void findByIdShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            service.findById(nonExistingId);
        });

        Mockito.verify(repository).findById(nonExistingId);
    }

    @Test
    void insertClientShouldReturnUserDTO() {
        UserDTO result = service.insertClient(Factory.createUserInsertDTO());

        Assertions.assertNotNull(result);
        Mockito.verify(repository).save(ArgumentMatchers.any());
    }

    @Test
    void insertAdminShouldReturnUserDTO() {
        UserDTO result = service.insertAdmin(Factory.createUserInsertDTO());

        Assertions.assertNotNull(result);
        Mockito.verify(repository).save(ArgumentMatchers.any());
    }
}
