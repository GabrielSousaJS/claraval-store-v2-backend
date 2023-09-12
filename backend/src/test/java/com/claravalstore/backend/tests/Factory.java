package com.claravalstore.backend.tests;

import com.claravalstore.backend.dto.*;
import com.claravalstore.backend.entities.*;
import com.claravalstore.backend.entities.enums.OrderStatus;
import com.claravalstore.backend.entities.pk.OrderItemPk;
import com.claravalstore.backend.projections.ProductProjection;
import org.springframework.orm.ObjectRetrievalFailureException;

import java.time.Instant;
import java.util.UUID;

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
        User user = new User(1L, "João Silva", Instant.parse("1990-07-20T10:30:00Z"), "joao.silva@gmail.com", "12345678");
        user.setAddress(createAddress());
        user.getPrivileges().add(createPrivilege());
        return user;
    }

    public static UserDTO createUserDTO() {
        return new UserDTO(createUser(), createAddress());
    }

    public static UserInsertDTO createUserInsertDTO() {
        return copyUserDtoToUserInsertDto(createUserDTO());
    }

    private static UserInsertDTO copyUserDtoToUserInsertDto(UserDTO dto) {
        UserInsertDTO userInsertDTO = new UserInsertDTO();
        userInsertDTO.setName(dto.getName());
        userInsertDTO.setEmail(dto.getEmail());
        userInsertDTO.setPassword(createUser().getPassword());
        userInsertDTO.setBirthDate(dto.getBirthDate());
        userInsertDTO.setAddress(dto.getAddress());
        userInsertDTO.getPrivileges().add(dto.getPrivileges().get(0));
        return userInsertDTO;
    }

    public static ProductProjection createProductProjection() {
        return new ProductProjection() {
            @Override
            public Long getId() {
                return 1L;
            }

            @Override
            public String getName() {
                return "Phone";
            }
        };
    }

    public static Order createOrder() {
        Order order = new Order(1L, Instant.parse("2023-08-05T00:00:00Z"), createOrderStatus(), createUser());
        OrderItem item = new OrderItem(order, createProduct(), 3, createProduct().getPrice());
        order.getItems().add(item);
        return order;
    }

    private static OrderStatus createOrderStatus() {
        return OrderStatus.PAGO;
    }

    public static OrderItem createOrderItem() {
        return new OrderItem(createOrder(), createProduct(), 3, createProduct().getPrice());
    }

    public static OrderItemDTO createOrderItemDTO() {
        return new OrderItemDTO(createOrderItem());
    }

    public static Payment createPayment() {
        return new Payment(1L, Instant.parse("2023-08-05T00:00:00Z"));
    }

    public static PaymentDTO createPaymentDTO() {
        return new PaymentDTO(createPayment());
    }

    public static OrderDTO createOrderDTO() {
        return new OrderDTO(createOrder());
    }

    public static OrderItemPk createOrderItemPk() {
        return new OrderItemPk(createOrder(), createProduct());
    }

    public static EmailDTO createEmailDTO() {
        return new EmailDTO("gabriela.oliveira@gmail.com");
    }

    public static NewPasswordDTO createNewPasswordDTO() {
        return new NewPasswordDTO(UUID.randomUUID().toString(), "12345678");
    }
}
