package com.claravalstore.backend.services;

import com.claravalstore.backend.dto.CategoryDTO;
import com.claravalstore.backend.entities.Category;
import com.claravalstore.backend.repositories.CategoryRepository;
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
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

@ExtendWith(SpringExtension.class)
public class CategoryServiceTests {

    @InjectMocks
    private CategoryService service;

    @Mock
    private CategoryRepository repository;

    private long existingId;
    private long nonExistingId;
    private long dependentId;
    private Category category;
    private CategoryDTO categoryDTO;

    @BeforeEach
    void setUp() {
        existingId = 1L;
        nonExistingId = 2L;
        dependentId = 3L;
        category = Factory.createCategory();
        categoryDTO = Factory.createCategoryDTO();
        List<Category> list = List.of(category);

        Mockito.when(repository.findAll()).thenReturn(list);

        Mockito.when(repository.findById(existingId)).thenReturn(Optional.of(category));
        Mockito.when(repository.findById(nonExistingId)).thenReturn(Optional.empty());

        Mockito.when(repository.getReferenceById(existingId)).thenReturn(category);
        Mockito.when(repository.getReferenceById(nonExistingId)).thenThrow(EntityNotFoundException.class);

        Mockito.when(repository.save(ArgumentMatchers.any())).thenReturn(category);

        Mockito.when(repository.existsById(existingId)).thenReturn(true);
        Mockito.when(repository.existsById(nonExistingId)).thenReturn(false);
        Mockito.when(repository.existsById(dependentId)).thenReturn(true);

        Mockito.doThrow(DataIntegrityViolationException.class).when(repository).deleteById(dependentId);
    }

    @Test
    void findAllShouldReturnListOfCategoryDTO() {
        List<CategoryDTO> result = service.findAll();

        Assertions.assertNotNull(result);
        Mockito.verify(repository).findAll();
    }

    @Test
    void findByIdShouldReturnCategoryDTOWhenIdExist() {
        CategoryDTO result = service.findById(existingId);

        Assertions.assertNotNull(result);
        Mockito.verify(repository).findById(existingId);
    }

    @Test
    void saveShouldReturnCategoryDTOWhenIdIsNull() {
        CategoryDTO result = service.insert(categoryDTO);

        Assertions.assertNotNull(result);
    }

    @Test
    void updateShouldReturnCategoryDTOWhenIdExist() {
        CategoryDTO result = service.update(existingId, categoryDTO);

        Assertions.assertNotNull(result);
        Mockito.verify(repository).getReferenceById(existingId);
        Mockito.verify(repository).save(category);
    }

    @Test
    void updateShouldThrowResourceNotFoundExceptionWhenIdDoesNotExists() {
        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            service.update(nonExistingId, categoryDTO);
        });

        Mockito.verify(repository).getReferenceById(nonExistingId);
    }

    @Test
    void deleteShouldDoNothingWhenIdExists() {
        Assertions.assertDoesNotThrow(() -> {
            service.delete(existingId);
        });

        Mockito.verify(repository).existsById(existingId);
        Mockito.verify(repository).deleteById(existingId);
    }

    @Test
    void deleteShouldThrowResourceNotFoundExceptionWhenIdDoesNotExists() {
        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            service.delete(nonExistingId);
        });

        Mockito.verify(repository).existsById(nonExistingId);
    }

    @Test
    void deleteShouldThrowDatabaseExceptionWhenDependentId() {
        Assertions.assertThrows(DatabaseException.class, () -> {
            service.delete(dependentId);
        });

        Mockito.verify(repository).existsById(dependentId);
    }
}