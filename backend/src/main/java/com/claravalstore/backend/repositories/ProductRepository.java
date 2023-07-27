package com.claravalstore.backend.repositories;

import com.claravalstore.backend.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("SELECT obj FROM Product obj JOIN FETCH obj.categories WHERE obj.id = :id")
    Optional<Product> searchById(Long id);

}
