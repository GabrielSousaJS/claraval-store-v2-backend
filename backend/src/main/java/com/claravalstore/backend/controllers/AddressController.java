package com.claravalstore.backend.controllers;

import com.claravalstore.backend.dto.AddressDTO;
import com.claravalstore.backend.services.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/addresses")
public class AddressController {

    @Autowired
    private AddressService service;

    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENT')")
    @PutMapping(value = "/{id}")
    public ResponseEntity<AddressDTO> update(@PathVariable Long id, @RequestBody AddressDTO dto) {
        dto = service.update(id, dto);
        return ResponseEntity.ok().body(dto);
    }

}
