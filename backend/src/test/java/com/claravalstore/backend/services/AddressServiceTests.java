package com.claravalstore.backend.services;

import com.claravalstore.backend.dto.AddressDTO;
import com.claravalstore.backend.entities.Address;
import com.claravalstore.backend.repositories.AddressRepository;
import com.claravalstore.backend.services.exceptions.ResourceNotFoundException;
import com.claravalstore.backend.tests.Factory;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
class AddressServiceTests {

    @InjectMocks
    private AddressService service;

    @Mock
    private AddressRepository repository;

    private long existingId;
    private long nonExistingId;
    private Address address;
    private AddressDTO addressDTO;

    @BeforeEach
    void setUp() {
        existingId = 1L;
        nonExistingId = 2L;
        address = Factory.createAddress();
        addressDTO = Factory.createAddressDTO();

        Mockito.when(repository.searchAddressByLoggedInUser(existingId)).thenReturn(address);

        Mockito.when(repository.getReferenceById(existingId)).thenReturn(address);
        Mockito.when(repository.getReferenceById(nonExistingId)).thenThrow(EntityNotFoundException.class);

        Mockito.when(repository.save(ArgumentMatchers.any())).thenReturn(address);
    }

    @Test
    void findAddressByLoggedInUserShouldReturnAddressDTOWhenIdExists() {
        AddressDTO result = service.findAddressByLoggedInUser(existingId);

        Assertions.assertNotNull(result);
        Mockito.verify(repository).searchAddressByLoggedInUser(existingId);
    }

    @Test
    void findAddressByLoggedInUserShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            service.findAddressByLoggedInUser(nonExistingId);
        });

        Mockito.verify(repository).searchAddressByLoggedInUser(nonExistingId);
    }

    @Test
    void insertShouldReturnAddressDTOWhenIdIsNull() {
        addressDTO.setId(null);
        Address result = service.insert(addressDTO);

        Assertions.assertNotNull(result.getId());
        Assertions.assertNotNull(result.getStreet());
        Assertions.assertNotNull(result.getCep());
        Assertions.assertNotNull(result.getNumber());
        Assertions.assertNotNull(result.getNeighborhood());
        Assertions.assertNotNull(result.getComplement());
        Assertions.assertNotNull(result.getCity());
        Assertions.assertNotNull(result.getState());
        Assertions.assertNotNull(result.getCountry());
    }

    @Test
    void updateShouldReturnAddressDTOWhenIdExists() {
        AddressDTO result = service.update(existingId, addressDTO);

        Assertions.assertNotNull(result);
        Mockito.verify(repository).getReferenceById(existingId);
        Mockito.verify(repository).save(address);
    }

    @Test
    void updateShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            service.update(nonExistingId, addressDTO);
        });

        Mockito.verify(repository).getReferenceById(nonExistingId);
    }
}
