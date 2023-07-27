package com.claravalstore.backend.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "tb_users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter @Setter
    private Long id;
    @Getter @Setter
    private String name;

    @Column(columnDefinition = "TIMESTAMP WITHOUT TIME ZONE")
    @Getter @Setter
    private Instant birthDate;
    @Getter @Setter
    private String email;
    @Getter @Setter
    private String password;

    @OneToOne
    @JoinTable(name = "tb_user_address",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "address_id"))
    @Getter @Setter
    private Address address;

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
}
