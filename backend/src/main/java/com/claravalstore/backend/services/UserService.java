package com.claravalstore.backend.services;

import com.claravalstore.backend.dto.UserDTO;
import com.claravalstore.backend.dto.UserMinDTO;
import com.claravalstore.backend.entities.Privilege;
import com.claravalstore.backend.entities.User;
import com.claravalstore.backend.projections.UserDetailsProjection;
import com.claravalstore.backend.repositories.UserRepository;
import com.claravalstore.backend.services.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository repository;

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

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        List<UserDetailsProjection> result = repository.searchUserAndPrivilegesByEmail(username);

        if (result.size() == 0)
            throw new UsernameNotFoundException("Usuário não encontrado");

        User user = new User();
        user.setEmail(username);
        user.setPassword(result.get(0).getPassword());

        for (UserDetailsProjection projection : result)
            user.addPrivilege(new Privilege(projection.getPrivilegeId(), projection.getAuthority()));

        return user;
    }
}
