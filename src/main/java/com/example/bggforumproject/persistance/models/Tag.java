package com.example.bggforumproject.persistance.models;

import com.example.bggforumproject.persistance.models.base.BaseEntity;

public class Tag extends BaseEntity {

    private String name;

    public Tag() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
