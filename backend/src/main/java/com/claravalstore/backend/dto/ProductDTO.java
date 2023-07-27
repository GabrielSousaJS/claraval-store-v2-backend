package com.claravalstore.backend.dto;

import com.claravalstore.backend.entities.Category;
import com.claravalstore.backend.entities.Product;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ProductDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Getter @Setter
    private Long id;

    @NotBlank(message = "Campo obrigatório")
    @Getter @Setter
    private String name;

    @NotBlank(message = "Campo obrigatório")
    @Getter @Setter
    private String description;

    @Positive(message = "Informe um valor positivo")
    @Getter @Setter
    private Double price;

    @Positive(message = "Informe um valor positivo")
    @Getter @Setter
    private Integer quantity;
    @Getter @Setter
    private String imgUrl;

    @Getter
    private List<CategoryDTO> categories = new ArrayList<>();

    public ProductDTO() {
    }

    public ProductDTO(Product entity) {
        this.id = entity.getId();
        this.name = entity.getName();
        this.description = entity.getDescription();
        this.price = entity.getPrice();
        this.quantity = entity.getQuantity();
        this.imgUrl = entity.getImgUrl();
    }

    public ProductDTO(Product entity, Set<Category> categories) {
        this(entity);
        categories.forEach(cat -> this.categories.add(new CategoryDTO(cat)));
    }
}
