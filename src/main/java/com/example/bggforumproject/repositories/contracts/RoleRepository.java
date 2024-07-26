package com.example.bggforumproject.repositories.contracts;

import com.example.bggforumproject.models.Role;

import java.util.List;


public interface RoleRepository {
    Role findByAuthority(String role);

    void create(Role role);

    List<Role> findAll();
}
