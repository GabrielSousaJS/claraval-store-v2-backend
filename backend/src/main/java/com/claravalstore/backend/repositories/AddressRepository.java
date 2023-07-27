package com.claravalstore.backend.repositories;

import com.claravalstore.backend.entities.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface AddressRepository extends JpaRepository<Address, Long> {

    @Query(nativeQuery = true, value = """
            SELECT *
            FROM tb_user_address
            INNER JOIN tb_address ON tb_address.id = tb_user_address.address_id
            WHERE tb_user_address.user_id = :id
            """)
    Address searchAddressByLoggedInUser(Long id);

}
