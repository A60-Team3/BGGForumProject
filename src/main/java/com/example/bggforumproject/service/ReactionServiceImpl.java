package com.example.bggforumproject.service;

import com.example.bggforumproject.exceptions.EntityDuplicateException;
import com.example.bggforumproject.exceptions.EntityNotFoundException;
import com.example.bggforumproject.exceptions.PostMismatchException;
import com.example.bggforumproject.helpers.AuthorizationHelper;
import com.example.bggforumproject.models.Post;
import com.example.bggforumproject.models.Reaction;
import com.example.bggforumproject.models.User;
import com.example.bggforumproject.models.enums.ReactionType;
import com.example.bggforumproject.repositories.contracts.PostRepository;
import com.example.bggforumproject.repositories.contracts.ReactionRepository;
import com.example.bggforumproject.service.contacts.ReactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReactionServiceImpl implements ReactionService {

    private final ReactionRepository reactionRepository;
    private final PostRepository postRepository;
    private final AuthorizationHelper authorizationHelper;

    @Autowired
    public ReactionServiceImpl(ReactionRepository reactionRepository, PostRepository postRepository, AuthorizationHelper authorizationHelper) {
        this.reactionRepository = reactionRepository;
        this.postRepository = postRepository;
        this.authorizationHelper = authorizationHelper;
    }

    @Override
    public List<Reaction> getAll(long postId) {
        postRepository.get(postId);
        return reactionRepository.getAllForSpecificPost(postId);
    }

    @Override
    public List<Reaction> getAllByUser(long userId) {
        return reactionRepository.getAllByUser(userId);
    }

    @Override
    public Reaction getByPostAndUser(long userId, long postId) {
        return reactionRepository.getByPostAndUser(userId, postId);
    }

    @Override
    public long getLikesCount(long postId) {
        postRepository.get(postId);
        return reactionRepository.getLikesCount(postId);
    }

    @Override
    public long getDislikesCount(long postId) {
        postRepository.get(postId);
        return reactionRepository.getDislikesCount(postId);
    }

    @Override
    public Reaction get(long id) {
        return reactionRepository.get(id);
    }

    @Override
    public void create(Reaction reaction, User user, long postId) {
        Post post = postRepository.get(postId);

        boolean isDuplicate = true;
        try{
            reactionRepository.getByPostAndUser(user.getId(), post.getId());
        } catch (EntityNotFoundException e){
            isDuplicate = false;
        }

        if (isDuplicate){
            throw new EntityDuplicateException("Reaction", "userId", String.valueOf(user.getId()));
        }

        reaction.setPostId(post);
        reaction.setUserId(user);

        reactionRepository.create(reaction);
    }

    @Override
    public Reaction update(long reactionId, User user, long postId, ReactionType newReaction) {
        postRepository.get(postId);
        Reaction reaction = reactionRepository.get(reactionId);

        authorizationHelper.checkOwnership(reaction, user);

        if (reaction.getPostId().getId() != postId) {
            throw new PostMismatchException("Reaction does not belong to the specified post");
        }

        if (reaction.getReactionType().equals(newReaction)) {
            throw new EntityDuplicateException(String.format("User has already %s the post", newReaction));
        }

        reaction.setReactionType(newReaction);

        reactionRepository.update(reaction);

        return reaction;
    }

    @Override
    public void delete(long reactionId, User user, long postId) {
        postRepository.get(postId);
        Reaction reaction = reactionRepository.get(reactionId);

        authorizationHelper.checkOwnership(reaction, user);

        if (reaction.getPostId().getId() != postId) {
            throw new PostMismatchException("Reaction does not belong to the specified post");
        }

        reactionRepository.delete(reaction);
    }
}
