package com.example.bggforumproject.dtos.response;

import java.util.List;

public class TagOutDTO {

    private String name;

    private List<String> posts;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getPosts() {
        return posts;
    }

    public void setPosts(List<String> posts) {
        this.posts = posts;
    }
}
