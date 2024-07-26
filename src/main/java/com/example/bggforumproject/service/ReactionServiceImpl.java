package com.example.bggforumproject.service;

import com.example.bggforumproject.models.Post;
import com.example.bggforumproject.models.Reaction;
import com.example.bggforumproject.models.User;
import com.example.bggforumproject.repositories.contracts.PostRepository;
import com.example.bggforumproject.repositories.contracts.ReactionRepository;
import com.example.bggforumproject.exceptions.AuthorizationException;
import com.example.bggforumproject.exceptions.EntityDuplicateException;
import com.example.bggforumproject.exceptions.EntityNotFoundException;
import com.example.bggforumproject.service.contacts.ReactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReactionServiceImpl implements ReactionService {

    private static final String MODIFY_REACTION_ERROR_MESSAGE = "Reactions can only be modified or removed by their creator!";

    private final ReactionRepository reactionRepository;
    private final PostRepository postRepository;

    @Autowired
    public ReactionServiceImpl(ReactionRepository reactionRepository, PostRepository postRepository) {
        this.reactionRepository = reactionRepository;
        this.postRepository = postRepository;
    }

    @Override
    public List<Reaction> getAll(long postId) {
        postRepository.get(postId);
        return reactionRepository.getAll(postId);
    }

    @Override
    public Reaction get(long id) {
        return reactionRepository.get(id);
    }

    @Override
    public void create(Reaction reaction, User user, Post post) {
        boolean isDuplicate = true;
        try{
            reactionRepository.getByPostAndUser(user.getId(), post.getId());
        } catch (EntityNotFoundException e){
            isDuplicate = false;
        }

        if (isDuplicate){
            throw new EntityDuplicateException("Reaction", "userId", String.valueOf(user.getId()));
        }

        reaction.setUserId(user);
        reactionRepository.create(reaction);
    }

    @Override
    public void update(Reaction reaction, User user, Post post) {
        checkModifyPermissions(reaction.getId(), user);

        boolean isDuplicate = true;
        try{
            Reaction existingReaction = reactionRepository.getByPostAndUser(user.getId(), post.getId());
            if(existingReaction.getId() == reaction.getId()){
                isDuplicate = false;
            }
        } catch (EntityNotFoundException e){
            isDuplicate = false;
        }

        if (isDuplicate){
            throw new EntityDuplicateException("Reaction", "userId", String.valueOf(user.getId()));
        }

        reactionRepository.update(reaction);
    }

    @Override
    public void delete(long id, User user) {
        checkModifyPermissions(id, user);

        reactionRepository.delete(id);
    }

    private void checkModifyPermissions(long reactionId, User user) {
        Reaction reaction = reactionRepository.get(reactionId);
        if (reaction.getUserId().getId() != user.getId()) {
            throw new AuthorizationException(MODIFY_REACTION_ERROR_MESSAGE);
        }
    }
}
