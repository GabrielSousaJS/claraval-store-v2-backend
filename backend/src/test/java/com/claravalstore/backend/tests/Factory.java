package com.claravalstore.backend.tests;

import com.claravalstore.backend.dto.*;
import com.claravalstore.backend.entities.*;

import java.time.Instant;

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

    public static Privilege createPrivilege() {
        return new Privilege(1L, "ROLE_ADMIN");
    }

    public static Address createAddress() {
        return new Address(1L, "Rua 1", "12345-678", 123, "Bairro 1", "Complemento 1", "Cidade 1", "Estado 1", "País 1");
    }

    public static AddressDTO createAddressDTO() {
        return new AddressDTO(createAddress());
    }

    public static User createUser() {
        User user = new User(1L, "João Silva", Instant.parse("1990-07-20T10:30:00Z"), "joao.silva@gmail.com", "123456");
        user.setAddress(createAddress());
        user.getPrivileges().add(createPrivilege());
        return user;
    }

    public static UserDTO createUserDTO() {
        return new UserDTO(createUser(), createAddress());
    }
}
