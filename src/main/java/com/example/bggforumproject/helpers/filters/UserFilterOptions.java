package com.example.bggforumproject.helpers.filters;

import com.example.bggforumproject.models.enums.RoleType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class UserFilterOptions {

    private final Optional<String> firstName;
    private final Optional<String> lastName;
    private final Optional<String> email;
    private final Optional<String> username;
    private final Optional<String> registeredCondition;
    private final Optional<LocalDateTime> registered;
    private final Optional<String> updatedCondition;
    private final Optional<LocalDateTime> updated;
    private final Optional<Boolean> isBlocked;
    private final Optional<Boolean> isDeleted;
    private final Optional<List<RoleType>> authority;
    private final Optional<String> phoneNumber;
    private final Optional<String> sortBy;
    private final Optional<String> sortOrder;

    public UserFilterOptions(String firstName, String lastName, String email, String username,
                             String registeredCondition, LocalDateTime registered,
                             String updatedCondition, LocalDateTime updated,
                             Boolean isBlocked, Boolean isDeleted,
                             List<RoleType> authority, String phoneNumber,
                             String sortBy, String sortOrder) {

        this.firstName = Optional.ofNullable(firstName);
        this.lastName = Optional.ofNullable(lastName);
        this.email = Optional.ofNullable(email);
        this.username = Optional.ofNullable(username);
        this.registeredCondition = Optional.ofNullable(registeredCondition);
        this.registered = Optional.ofNullable(registered);
        this.updatedCondition = Optional.ofNullable(updatedCondition);
        this.updated = Optional.ofNullable(updated);
        this.isBlocked = Optional.ofNullable(isBlocked);
        this.isDeleted = Optional.ofNullable(isDeleted);
        this.authority = Optional.ofNullable(authority);
        this.phoneNumber = Optional.ofNullable(phoneNumber);
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

    public Optional<LocalDateTime> getRegistered() {
        return registered;
    }

    public Optional<LocalDateTime> getUpdated() {
        return updated;
    }

    public Optional<Boolean> getIsBlocked() {
        return isBlocked;
    }

    public Optional<Boolean> getIsDeleted() {
        return isDeleted;
    }

    public Optional<List<RoleType>> getAuthority() {
        return authority;
    }

    public Optional<String> getSortBy() {
        return sortBy;
    }

    public Optional<String> getSortOrder() {
        return sortOrder;
    }

    public Optional<String> getRegisteredCondition() {
        return registeredCondition;
    }

    public Optional<String> getUpdatedCondition() {
        return updatedCondition;
    }

    public Optional<String> getPhoneNumber() {
        return phoneNumber;
    }
}
