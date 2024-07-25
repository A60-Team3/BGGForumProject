package com.example.bggforumproject.persistance.repositories;

import com.example.bggforumproject.persistance.models.Role;
import com.example.bggforumproject.persistance.models.enums.RoleType;

import java.util.List;


public interface RoleRepository {
    Role findByAuthority(String role);

    void create(Role role);

    List<Role> findAll();
}
