package com.example.bggforumproject.models;

import com.example.bggforumproject.models.base.BaseEntity;
import com.example.bggforumproject.models.enums.RoleType;
import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;

import java.util.Objects;

@Entity
@Table(name = "roles")
public class Role extends BaseEntity implements GrantedAuthority {

    @Enumerated(EnumType.STRING)
    @Column(name = "role_type")
    private RoleType authority;

    public Role(RoleType type) {
        this.authority = type;
    }

    public Role() {
    }

    public void setAuthority(RoleType type) {
        this.authority = type;
    }

    @Override
    public String getAuthority() {
        return authority.name();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Role role = (Role) o;
        return Objects.equals(authority, role.authority);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(authority);
    }
}
