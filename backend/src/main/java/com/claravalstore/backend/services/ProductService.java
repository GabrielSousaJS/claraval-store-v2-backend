package com.claravalstore.backend.services;

import com.claravalstore.backend.dto.CategoryDTO;
import com.claravalstore.backend.dto.ProductDTO;
import com.claravalstore.backend.entities.Category;
import com.claravalstore.backend.entities.OrderItem;
import com.claravalstore.backend.entities.Product;
import com.claravalstore.backend.projections.ProductProjection;
import com.claravalstore.backend.repositories.CategoryRepository;
import com.claravalstore.backend.repositories.ProductRepository;
import com.claravalstore.backend.services.exceptions.DatabaseException;
import com.claravalstore.backend.services.exceptions.ResourceNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

@Service
public class ProductService {

    @Autowired
    private ProductRepository repository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public Page<ProductDTO> findAllPaged(
            String name, String categoryId, Pageable pageable
    ) {
        List<Long> categoryIds = List.of();

        if (!"0".equals(categoryId))
            categoryIds = Stream.of(categoryId.split(",")).map(Long::parseLong).toList();

        Page<ProductProjection> page = repository.searchProducts(categoryIds, name, pageable);
        List<Long> productIds = page.map(ProductProjection::getId).toList();

        List<Product> entities = repository.searchProductsWithCategories(productIds);
        List<ProductDTO> dtos = entities.stream().map(product -> new ProductDTO(product, product.getCategories())).toList();

        return new PageImpl<>(dtos, page.getPageable(), page.getTotalElements());
    }

    @Transactional(readOnly = true)
    public ProductDTO findById(Long id) {
        Optional<Product> obj = repository.searchById(id);
        Product entity = obj.orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado"));
        return new ProductDTO(entity, entity.getCategories());
    }

    @Transactional
    public ProductDTO insert(ProductDTO dto) {
        Product entity = new Product();
        copyDtoToEntity(entity, dto);
        entity = repository.save(entity);
        return new ProductDTO(entity, entity.getCategories());
    }

    @Transactional
    public ProductDTO update(Long id, ProductDTO dto) {
        try {
            Product entity = repository.getReferenceById(id);
            copyDtoToEntity(entity, dto);
            entity = repository.save(entity);
            return new ProductDTO(entity, entity.getCategories());
        } catch (EntityNotFoundException e) {
            throw new ResourceNotFoundException("Produto não encontrado para atualizar");
        }
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public void delete(Long id) {
        if (!repository.existsById(id))
            throw new ResourceNotFoundException("Produto não encontrado para deletar");
        try {
            repository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseException("Falha de integridade referencial");
        }
    }

    private void copyDtoToEntity(Product entity, ProductDTO dto) {
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setPrice(dto.getPrice());
        entity.setQuantity(dto.getQuantity());
        entity.setImgUrl(dto.getImgUrl());

        entity.getCategories().clear();
        for (CategoryDTO catDto : dto.getCategories()) {
            Category category = categoryRepository.getReferenceById(catDto.getId());
            entity.getCategories().add(category);
        }
    }

    protected void updateQuantityOfProducts(Set<OrderItem> items) {
        for (OrderItem item : items) {
            Product product = repository.getReferenceById(item.getProduct().getId());
            product.setQuantity(product.getQuantity() - item.getQuantity());
            update(product.getId(), new ProductDTO(product, product.getCategories()));
        }
    }
}
