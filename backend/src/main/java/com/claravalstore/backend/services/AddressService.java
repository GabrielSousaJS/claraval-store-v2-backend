package com.claravalstore.backend.services;

import com.claravalstore.backend.dto.AddressDTO;
import com.claravalstore.backend.entities.Address;
import com.claravalstore.backend.repositories.AddressRepository;
import com.claravalstore.backend.services.exceptions.ResourceNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AddressService {

    @Autowired
    private AddressRepository repository;

    protected Address insert(AddressDTO dto) {
        Address entity = new Address();
        copyDtoToEntity(entity, dto);
        entity = repository.save(entity);
        return entity;
    }

    @Transactional
    public AddressDTO update(Long id, AddressDTO dto) {
        try {
            Address entity = repository.getReferenceById(id);
            copyDtoToEntity(entity, dto);
            entity = repository.save(entity);
            return new AddressDTO(entity);
        } catch (EntityNotFoundException e) {
            throw new ResourceNotFoundException("Endereço não encontrado para atualização");
        }
    }

    private void copyDtoToEntity(Address entity, AddressDTO dto) {
        entity.setStreet(dto.getStreet());
        entity.setCep(dto.getCep());
        entity.setNumber(dto.getNumber());
        entity.setNeighborhood(dto.getNeighborhood());
        entity.setComplement(dto.getComplement());
        entity.setCity(dto.getCity());
        entity.setState(dto.getState());
        entity.setCountry(dto.getCountry());
    }
}
