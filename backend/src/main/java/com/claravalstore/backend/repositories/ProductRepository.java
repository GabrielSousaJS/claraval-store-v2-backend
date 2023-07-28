package com.claravalstore.backend.repositories;

import com.claravalstore.backend.entities.Product;
import com.claravalstore.backend.projections.ProductProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query(nativeQuery = true, value = """
            SELECT DISTINCT tb_products.id, tb_products.name
            FROM tb_products INNER JOIN tb_product_category ON tb_products.id = tb_product_category.product_id
            WHERE (:categoryIds IS NULL OR tb_product_category.category_id IN :categoryIds)
            AND LOWER(tb_products.name) LIKE LOWER(CONCAT('%',:name,'%'))
            ORDER BY tb_products.name
            """, countQuery = """
            SELECT COUNT(*) FROM(
            SELECT DISTINCT tb_products.id, tb_products.name
                        FROM tb_products INNER JOIN tb_product_category ON tb_products.id = tb_product_category.product_id
                        WHERE (:categoryIds IS NULL OR tb_product_category.category_id IN :categoryIds)
                        AND LOWER(tb_products.name) LIKE LOWER(CONCAT('%',:name,'%'))
                        ORDER BY tb_products.name) AS tb_result
            """)
    Page<ProductProjection> searchProducts(List<Long> categoryIds, String name, Pageable pageable);

    @Query("SELECT obj FROM Product obj JOIN FETCH obj.categories WHERE obj.id IN :productIds ORDER BY obj.name")
    List<Product> searchProductsWithCategories(List<Long> productIds);

    @Query("SELECT obj FROM Product obj JOIN FETCH obj.categories WHERE obj.id = :id")
    Optional<Product> searchById(Long id);

}
