package com.claravalstore.backend.services;

import com.claravalstore.backend.dto.PasswordUpdateDTO;
import com.claravalstore.backend.dto.UserDTO;
import com.claravalstore.backend.dto.UserInsertDTO;
import com.claravalstore.backend.dto.UserMinDTO;
import com.claravalstore.backend.entities.Privilege;
import com.claravalstore.backend.entities.User;
import com.claravalstore.backend.projections.UserDetailsProjection;
import com.claravalstore.backend.repositories.UserRepository;
import com.claravalstore.backend.services.exceptions.IncorrectPassword;
import com.claravalstore.backend.services.exceptions.ResourceNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository repository;

    @Autowired
    private PrivilegeService privilegeService;

    @Autowired
    private AddressService addressService;

    @Autowired
    private AuthService authService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public Page<UserMinDTO> findAllPaged(Pageable pageable) {
        Page<User> page = repository.findAll(pageable);
        return page.map(UserMinDTO::new);
    }

    @Transactional(readOnly = true)
    public UserDTO findById(Long id) {
        Optional<User> obj = repository.findById(id);
        User entity = obj.orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));
        return new UserDTO(entity, entity.getAddress());
    }

    @Transactional(readOnly = true)
    public UserDTO findLoggedInProfile() {
        User entity = authService.authenticated();
        return new UserDTO(entity, entity.getAddress());
    }

    @Transactional
    public UserDTO insertClient(UserInsertDTO dto) {
        User entity = new User();

        copyDtoToEntity(entity, dto);
        entity.setAddress(addressService.insert(dto.getAddress()));
        entity.addPrivilege(privilegeService.clientPrivilege());
        entity.setPassword(passwordEncoder.encode(dto.getPassword()));

        entity = repository.save(entity);
        return new UserDTO(entity, entity.getAddress());
    }

    @Transactional
    public UserDTO insertAdmin(UserInsertDTO dto) {
        User entity = new User();

        copyDtoToEntity(entity, dto);
        entity.setAddress(addressService.insert(dto.getAddress()));
        entity.addPrivilege(privilegeService.clientPrivilege());
        entity.addPrivilege(privilegeService.adminPrivilege());
        entity.setPassword(passwordEncoder.encode(dto.getPassword()));

        entity = repository.save(entity);
        return new UserDTO(entity, entity.getAddress());
    }

    @Transactional
    public UserDTO update(Long id, UserDTO dto) {
        try {
            User entity = repository.getReferenceById(id);

            copyDtoToEntity(entity, dto);
            repository.save(entity);
            return new UserDTO(entity, entity.getAddress());
        } catch (EntityNotFoundException e) {
            throw new ResourceNotFoundException("Usuário não encontrado para atualização");
        }
    }

    @Transactional
    public void updatePassword(PasswordUpdateDTO dto) {
        User entity = authService.authenticated();
        if (passwordEncoder.matches(dto.getOldPassword(), entity.getPassword())) {
            entity.setPassword(passwordEncoder.encode(dto.getNewPassword()));
            repository.save(entity);
        } else
            throw new IncorrectPassword("A senha atual está incorreta");
    }

    private void copyDtoToEntity(User entity, UserDTO dto) {
        entity.setName(dto.getName());
        entity.setEmail(dto.getEmail());
        entity.setBirthDate(dto.getBirthDate());
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        List<UserDetailsProjection> result = repository.searchUserAndPrivilegesByEmail(username);

        if (result.isEmpty())
            throw new UsernameNotFoundException("Usuário não encontrado");

        User user = new User();
        user.setEmail(username);
        user.setPassword(result.get(0).getPassword());

        for (UserDetailsProjection projection : result)
            user.addPrivilege(new Privilege(projection.getPrivilegeId(), projection.getAuthority()));

        return user;
    }
}
