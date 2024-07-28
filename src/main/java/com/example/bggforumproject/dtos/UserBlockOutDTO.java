package com.example.bggforumproject.dtos;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "This DTO holds user's name and block status.", allowableValues = {"fullName", "isBlocked"})
public class UserBlockOutDTO {
    private String fullName;
    private boolean isBlocked;

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public boolean isBlocked() {
        return isBlocked;
    }

    public void setBlocked(boolean blocked) {
        isBlocked = blocked;
    }
}
