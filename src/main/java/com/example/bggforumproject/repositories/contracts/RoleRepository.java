package com.example.bggforumproject.repositories.contracts;

import com.example.bggforumproject.models.Role;
import com.example.bggforumproject.models.enums.RoleType;

import java.util.List;


public interface RoleRepository {
    Role getByAuthority(RoleType role);

    void create(Role role);

    List<Role> findAll();
}
