package com.example.bggforumproject.persistance.repositories;

import com.example.bggforumproject.persistance.models.Role;
import com.example.bggforumproject.persistance.models.enums.RoleType;


public interface RoleRepository {
    Role findByAuthority(String role);
}
