package com.example.bggforumproject.service.contacts;

import com.example.bggforumproject.models.Role;
import com.example.bggforumproject.models.enums.RoleType;

import java.util.List;

public interface RoleService {
    List<Role> getAll();
    Role getRole(RoleType type);
}
