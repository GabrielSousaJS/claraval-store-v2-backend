package com.claravalstore.backend.services;

import com.claravalstore.backend.entities.Privilege;
import com.claravalstore.backend.repositories.PrivilegeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PrivilegeService {

    @Autowired
    private PrivilegeRepository repository;

    @Transactional(readOnly = true)
    public Privilege clientPrivilege() {
        return repository.findPrivilegeByAuthority("ROLE_CLIENT");
    }

    @Transactional(readOnly = true)
    public Privilege adminPrivilege() {
        return repository.findPrivilegeByAuthority("ROLE_ADMIN");
    }
}
