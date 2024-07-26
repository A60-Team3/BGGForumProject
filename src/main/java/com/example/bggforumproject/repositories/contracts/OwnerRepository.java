package com.example.bggforumproject.repositories.contracts;

public interface OwnerRepository<T> {
    T get(long id);
}
