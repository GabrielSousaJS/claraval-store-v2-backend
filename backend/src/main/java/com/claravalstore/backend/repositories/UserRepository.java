package com.claravalstore.backend.repositories;

import com.claravalstore.backend.entities.User;
import com.claravalstore.backend.projections.UserDetailsProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByName(String name);

    @Query(nativeQuery = true, value = """
                SELECT tb_users.email AS username, tb_users.password, tb_privilege.id AS privilegeId, 
                tb_privilege.authority FROM tb_users INNER JOIN tb_user_privilege ON tb_users.id = tb_user_privilege.user_id
                INNER JOIN tb_privilege ON tb_privilege.id = tb_user_privilege.privilege_id WHERE tb_users.email = :email
            """)
    List<UserDetailsProjection> searchUserAndPrivilegesByEmail(String email);

}
