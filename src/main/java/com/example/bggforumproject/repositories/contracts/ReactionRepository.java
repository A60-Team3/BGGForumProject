package com.example.bggforumproject.repositories.contracts;

import com.example.bggforumproject.models.Reaction;

import java.util.List;

public interface ReactionRepository extends OwnerRepository<Reaction> {

    List<Reaction> getAll(long postId);

    int getLikesCount(long postId);

    int getDislikesCount(long postId);

    Reaction get(long id);

    Reaction getByPostAndUser(long userId, long postId);

    void create(Reaction reaction);

    void update(Reaction reaction);

    void delete(Reaction id);

}
