package com.example.bggforumproject.presentation.helpers;

import java.util.Optional;

public class UserFilterOptions {

    private final Optional<String> firstName;
    private final Optional<String> lastName;
    private final Optional<String> email;
    private final Optional<String> username;
    private final Optional<String> registered;
    private final Optional<String> updated;
    private final Optional<Boolean> isBlocked;
    private final Optional<Boolean> isDeleted;
    private final Optional<String> authority;
    private final Optional<String> sortBy;
    private final Optional<String> sortOrder;

    public UserFilterOptions(String firstName, String lastName, String email, String username,
                             String registered, String updated, Boolean isBlocked, Boolean isDeleted,
                             String authority, String sortBy, String sortOrder) {

        this.firstName = Optional.ofNullable(firstName);
        this.lastName = Optional.ofNullable(lastName);
        this.email = Optional.ofNullable(email);
        this.username = Optional.ofNullable(username);
        this.registered = Optional.ofNullable(registered);
        this.updated = Optional.ofNullable(updated);
        this.isBlocked=Optional.ofNullable(isBlocked);
        this.isDeleted=Optional.ofNullable(isDeleted);
        this.authority = Optional.ofNullable(authority);
        this.sortBy = Optional.ofNullable(sortBy);
        this.sortOrder = Optional.ofNullable(sortOrder);
    }

    public Optional<String> getFirstName() {
        return firstName;
    }

    public Optional<String> getLastName() {
        return lastName;
    }

    public Optional<String> getEmail() {
        return email;
    }

    public Optional<String> getUsername() {
        return username;
    }

    public Optional<String> getRegistered() {
        return registered;
    }

    public Optional<String> getUpdated() {
        return updated;
    }

    public Optional<Boolean> getIsBlocked() {
        return isBlocked;
    }

    public Optional<Boolean> getIsDeleted() {
        return isDeleted;
    }

    public Optional<String> getAuthority() {
        return authority;
    }

    public Optional<String> getSortBy() {
        return sortBy;
    }

    public Optional<String> getSortOrder() {
        return sortOrder;
    }
}
