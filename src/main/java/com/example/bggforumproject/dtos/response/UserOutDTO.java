package com.example.bggforumproject.dtos.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Set;
@Schema(description = "This DTO holds concise user information.",
        allowableValues = {"fullName", "email", "registeredAt","updatedAt", "roles"})
public class UserOutDTO {

    private String fullName;
    private String email;
    private String registeredAt;
    private String updatedAt;
    private Set<String> roles;

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRegisteredAt() {
        return registeredAt;
    }

    public void setRegisteredAt(String registeredAt) {
        this.registeredAt = registeredAt;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }
}
