package com.claravalstore.backend.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Instant;
import java.util.*;

@Entity
@Table(name = "tb_users")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter @Setter
    private Long id;
    @Getter @Setter
    private String name;

    @Column(columnDefinition = "TIMESTAMP WITHOUT TIME ZONE")
    @Getter @Setter
    private Instant birthDate;

    @Column(unique = true)
    @Getter @Setter
    private String email;
    @Getter @Setter
    private String password;

    @OneToOne
    @Getter @Setter
    private Address address;

    @OneToMany(mappedBy = "client")
    @Getter
    private List<Order> orders = new ArrayList<>();

    @ManyToMany
    @JoinTable(name = "tb_user_privilege",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "privilege_id"))
    @Getter
    private Set<Privilege> privileges = new HashSet<>();

    public User() {
    }

    public User(Long id, String name, Instant birthDate, String email, String password) {
        this.id = id;
        this.name = name;
        this.birthDate = birthDate;
        this.email = email;
        this.password = password;
    }

    public void addPrivilege(Privilege privilege) {
        privileges.add(privilege);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return privileges.stream()
                .map(privilege -> new SimpleGrantedAuthority(privilege.getAuthority()))
                .toList();
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
