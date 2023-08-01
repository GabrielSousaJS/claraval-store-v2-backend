package com.claravalstore.backend.repositories;

import com.claravalstore.backend.entities.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface AddressRepository extends JpaRepository<Address, Long> {

    @Query("SELECT obj FROM Address obj WHERE obj.user.id = :id")
    Address searchAddressByLoggedInUser(Long id);

}
