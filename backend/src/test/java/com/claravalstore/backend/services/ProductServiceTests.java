package com.claravalstore.backend.services;

import com.claravalstore.backend.dto.ProductDTO;
import com.claravalstore.backend.entities.Category;
import com.claravalstore.backend.entities.Product;
import com.claravalstore.backend.repositories.CategoryRepository;
import com.claravalstore.backend.repositories.ProductRepository;
import com.claravalstore.backend.services.exceptions.DatabaseException;
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
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

@ExtendWith(SpringExtension.class)
class ProductServiceTests {

    @InjectMocks
    private ProductService service;

    @Mock
    private ProductRepository repository;

    @Mock
    private CategoryRepository categoryRepository;

    private long existingId;
    private long nonExistingId;
    private long dependentId;
    private Product product;
    private ProductDTO productDTO;
    private Category category;
    private PageImpl<Product> page;

    @BeforeEach
    void setUp() {
        existingId = 1L;
        nonExistingId = 2L;
        dependentId = 3L;
        product = Factory.createProduct();
        productDTO = Factory.createProductDTO();
        category = Factory.createCategory();
        page = new PageImpl<>(List.of(product));

        Mockito.when(repository.searchAllPaged(ArgumentMatchers.any())).thenReturn(page);

        Mockito.when(repository.save(ArgumentMatchers.any())).thenReturn(product);

        Mockito.when(repository.searchById(existingId)).thenReturn(Optional.of(product));
        Mockito.when(repository.searchById(nonExistingId)).thenReturn(Optional.empty());

        Mockito.when(repository.getReferenceById(existingId)).thenReturn(product);
        Mockito.when(repository.getReferenceById(nonExistingId)).thenThrow(EntityNotFoundException.class);

        Mockito.when(categoryRepository.getReferenceById(existingId)).thenReturn(category);
        Mockito.when(categoryRepository.getReferenceById(nonExistingId)).thenThrow(EntityNotFoundException.class);

        Mockito.when(repository.existsById(existingId)).thenReturn(true);
        Mockito.when(repository.existsById(nonExistingId)).thenReturn(false);
        Mockito.when(repository.existsById(dependentId)).thenReturn(true);

        Mockito.doThrow(DataIntegrityViolationException.class).when(repository).deleteById(dependentId);
    }

    @Test
    void findAllPagedShouldReturnPage() {
        Pageable pageable = PageRequest.of(0, 12);

        Page<ProductDTO> result = service.findAllPaged(pageable);

        Assertions.assertNotNull(result);
        Mockito.verify(repository).searchAllPaged(pageable);
    }

    @Test
    void findByIdShouldReturnProductDTOWhenIdExist() {
        ProductDTO result = service.findById(existingId);

        Assertions.assertNotNull(result);
        Mockito.verify(repository).searchById(existingId);
    }

    @Test
    void findByIdShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            service.findById(nonExistingId);
        });

        Mockito.verify(repository).searchById(nonExistingId);
    }

    @Test
    void saveShouldReturnProductDTOWhenIdIsNull() {
        productDTO.setId(null);
        ProductDTO result = service.insert(productDTO);

        Assertions.assertNotNull(result.getId());
        Assertions.assertNotNull(result.getName());
        Assertions.assertNotNull(result.getDescription());
        Assertions.assertNotNull(result.getPrice());
        Assertions.assertNotNull(result.getImgUrl());
        Assertions.assertNotNull(result.getCategories());
    }

    @Test
    void updateShouldReturnProductDTOWhenIdExists() {
        ProductDTO result = service.update(existingId, productDTO);

        Assertions.assertNotNull(result);
        Mockito.verify(repository).getReferenceById(existingId);
        Mockito.verify(repository).save(product);
    }

    @Test
    void updateShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            service.update(nonExistingId, productDTO);
        });

        Mockito.verify(repository, Mockito.times(1)).getReferenceById(nonExistingId);
    }

    @Test
    void deleteShouldDoNothingWhenIdExists() {
        Assertions.assertDoesNotThrow(() -> {
            service.delete(existingId);
        });

        Mockito.verify(repository).deleteById(existingId);
    }

    @Test
    void deleteShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            service.delete(nonExistingId);
        });
    }

    @Test
    void deleteShouldThrowDatabaseExceptionWhenDependentId() {
        Assertions.assertThrows(DatabaseException.class, () -> {
            service.delete(dependentId);
        });
    }
}
