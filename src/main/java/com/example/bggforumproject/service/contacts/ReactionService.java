package com.example.bggforumproject.service.contacts;

import com.example.bggforumproject.models.Reaction;
import com.example.bggforumproject.models.User;
import com.example.bggforumproject.models.enums.ReactionType;

import java.util.List;

public interface ReactionService {

    List<Reaction> getAll(long postId);

    List<Reaction> getAllByUser(long userId);

    long getLikesCount(long postId);

    long getDislikesCount(long postId);

    Reaction get(long id);

    void create(Reaction reaction, User user, long postId);

    Reaction update(long reactionId, User user, long postId, ReactionType reactionType);

    void delete(long id, User user, long postId);
}
