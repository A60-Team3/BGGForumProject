package com.example.bggforumproject.persistance.repositories;

import com.example.bggforumproject.persistance.models.Post;
import com.example.bggforumproject.persistance.models.Reaction;
import com.example.bggforumproject.persistance.models.User;

import java.util.List;

public interface ReactionRepository {

    List<Reaction> getAll(long postId);

    Reaction get(long id);

    Reaction getByPostAndUser(long userId, long postId);

    void create(Reaction reaction);

    void update(Reaction reaction);

    void delete(long id);

}
