package com.claravalstore.backend.tests;

import com.claravalstore.backend.dto.CategoryDTO;
import com.claravalstore.backend.entities.Category;

public class Factory {

    public static Category createCategory() {
        return new Category(1L, "Eletrônicos");
    }

    public static CategoryDTO createCategoryDTO() {
        return new CategoryDTO(createCategory());
    }
}
