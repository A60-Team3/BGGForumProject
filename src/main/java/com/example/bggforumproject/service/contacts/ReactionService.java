package com.example.bggforumproject.service.contacts;

import com.example.bggforumproject.models.Post;
import com.example.bggforumproject.models.Reaction;
import com.example.bggforumproject.models.User;

import java.util.List;

public interface ReactionService {

        List<Reaction> getAll(long postId);

        Reaction get(long id);

        void create(Reaction reaction, User user, Post post);

        void update(Reaction reaction, User user, Post post);

        void delete(long id, User user);
}
