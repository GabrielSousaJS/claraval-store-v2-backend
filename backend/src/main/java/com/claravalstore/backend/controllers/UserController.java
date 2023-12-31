package com.claravalstore.backend.controllers;

import com.claravalstore.backend.dto.PasswordUpdateDTO;
import com.claravalstore.backend.dto.UserDTO;
import com.claravalstore.backend.dto.UserInsertDTO;
import com.claravalstore.backend.dto.UserMinDTO;
import com.claravalstore.backend.services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping(value = "/api/users")
public class UserController {

    @Autowired
    private UserService service;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<Page<UserMinDTO>> findAllPaged(Pageable pageable) {
        Page<UserMinDTO> page = service.findAllPaged(pageable);
        return ResponseEntity.ok(page);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(value = "/all-admins")
    public ResponseEntity<List<UserMinDTO>> findAllAdmins() {
        List<UserMinDTO> list = service.findAllAdmins();
        return ResponseEntity.ok(list);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(value = "/{id}")
    public ResponseEntity<UserDTO> findById(@PathVariable Long id) {
        UserDTO dto = service.findById(id);
        return ResponseEntity.ok(dto);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENT')")
    @GetMapping(value = "/profile")
    public ResponseEntity<UserDTO> findLoggedInProfile() {
        UserDTO dto = service.findLoggedInProfile();
        return ResponseEntity.ok(dto);
    }

    @PostMapping
    public ResponseEntity<UserDTO> insertClient(@Valid @RequestBody UserInsertDTO dto) {
        UserDTO newDto = service.insertClient(dto);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(newDto.getId()).toUri();
        return ResponseEntity.created(uri).body(newDto);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(value = "/add-admin")
    public ResponseEntity<UserDTO> insertAdmin(@Valid @RequestBody UserInsertDTO dto) {
        UserDTO newDto = service.insertAdmin(dto);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(newDto.getId()).toUri();
        return ResponseEntity.created(uri).body(newDto);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENT')")
    @PutMapping(value = "/{id}")
    public ResponseEntity<UserDTO> update(@PathVariable Long id, @Valid @RequestBody UserDTO dto) {
        dto = service.update(id, dto);
        return ResponseEntity.ok(dto);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENT')")
    @PutMapping(value = "/password")
    public ResponseEntity<Void> updatePassword(@RequestBody PasswordUpdateDTO dto) {
        service.updatePassword(dto);
        return ResponseEntity.noContent().build();
    }
}
