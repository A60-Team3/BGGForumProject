package com.example.bggforumproject.models;

import com.example.bggforumproject.models.base.BaseEntity;
import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;

import java.util.Objects;

@Entity
@Table(name = "roles")
public class Role extends BaseEntity implements GrantedAuthority {
    //TODO return enum type
    @Column(name = "role_type")
    private String authority;

    public Role(String type) {
        this.authority = type;
    }

    public Role() {

    }

    public void setAuthority(String type) {
        this.authority = type;
    }

    @Override
    public String getAuthority() {
        return authority;
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
