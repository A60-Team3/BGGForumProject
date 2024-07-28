package com.example.bggforumproject.dtos;

import java.util.List;

public class TagsOutDTO {

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
