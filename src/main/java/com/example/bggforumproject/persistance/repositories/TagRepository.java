package com.example.bggforumproject.persistance.repositories;

import com.example.bggforumproject.persistance.models.Tag;

import java.util.List;

public interface TagRepository {

    List<Tag> get();

    Tag get(int id);

    Tag get(String name);

    void create(Tag tag);

    void update(Tag tag);

    void delete(int id);

}
