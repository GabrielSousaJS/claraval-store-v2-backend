package com.claravalstore.backend.tests;

import com.claravalstore.backend.dto.CategoryDTO;
import com.claravalstore.backend.dto.ProductDTO;
import com.claravalstore.backend.entities.Category;
import com.claravalstore.backend.entities.Product;

public class Factory {

    public static Product createProduct() {
        Product product = new Product(1L, "Phone", "Good Phone", 800.0, 18, "https://img.com/img.png");
        product.getCategories().add(new Category(1L, "Eletrônicos"));
        return product;
    }

    public static ProductDTO createProductDTO() {
        Product product = createProduct();
        return new ProductDTO(product, product.getCategories());
    }

    public static Category createCategory() {
        return new Category(1L, "Eletrônicos");
    }

    public static CategoryDTO createCategoryDTO() {
        return new CategoryDTO(createCategory());
    }
}
