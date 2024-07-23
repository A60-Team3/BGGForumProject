package com.example.bggforumproject.persistance.models;

import com.example.bggforumproject.persistance.models.base.BaseEntity;
import com.example.bggforumproject.persistance.models.enums.RoleType;
import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;

@Entity
@Table(name = "roles")
public class Role extends BaseEntity implements GrantedAuthority {

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


}
