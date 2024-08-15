package com.example.bggforumproject.repositories.contracts;

import com.example.bggforumproject.models.User;
import com.example.bggforumproject.helpers.filters.UserFilterOptions;
import org.springframework.data.domain.Page;

import java.util.List;

public interface UserRepository {
    List<User> getAll();

    Page<User> getAll(UserFilterOptions userFilterOptions, int pageIndex, int pageSize);

    User getById(long id);

    User getByUsername(String username);

    User getByEmail(String email);

    void create(User user);

    void update(User user);

    void delete(User user);
}
