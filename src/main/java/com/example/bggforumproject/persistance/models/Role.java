package com.example.bggforumproject.persistance.models;

import com.example.bggforumproject.persistance.models.base.BaseEntity;
import com.example.bggforumproject.persistance.models.enums.RoleType;
import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;

@Entity
@Table(name = "roles")
public class Role extends BaseEntity implements GrantedAuthority {

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


}
