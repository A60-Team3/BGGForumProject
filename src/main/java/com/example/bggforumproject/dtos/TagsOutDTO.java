package com.example.bggforumproject.dtos;

import java.util.Set;

public class TagsOutDTO {

    private String name;

    private Set<String> posts;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<String> getPosts() {
        return posts;
    }

    public void setPosts(Set<String> posts) {
        this.posts = posts;
    }
}
