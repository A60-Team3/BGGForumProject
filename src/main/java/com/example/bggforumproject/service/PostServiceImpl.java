package com.example.bggforumproject.service;

import com.example.bggforumproject.exceptions.AuthorizationException;
import com.example.bggforumproject.exceptions.EntityDuplicateException;
import com.example.bggforumproject.exceptions.EntityNotFoundException;
import com.example.bggforumproject.helpers.AuthorizationHelper;
import com.example.bggforumproject.helpers.filters.PostFilterOptions;
import com.example.bggforumproject.models.Post;
import com.example.bggforumproject.models.User;
import com.example.bggforumproject.repositories.contracts.PostRepository;
import com.example.bggforumproject.service.contacts.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PostServiceImpl implements PostService {

    private static final String MODIFY_POST_ERROR_MESSAGE = "Posts can only be modified by their creator!";
    private static final String DELETE_POST_ERROR_MESSAGE = "Posts can only be deleted by their creator, moderators or admins!";

    private final PostRepository postRepository;
    private final AuthorizationHelper authorizationHelper;

    @Autowired
    public PostServiceImpl(PostRepository postRepository, AuthorizationHelper authorizationHelper) {
        this.postRepository = postRepository;
        this.authorizationHelper = authorizationHelper;
    }

    @Override
    public List<Post> get(PostFilterOptions postFilterOptions) {
        return postRepository.get(postFilterOptions);
    }

    @Override
    public Post get(long id) {
        return postRepository.get(id);
    }

    @Override
    public Post get(String title) {
        return postRepository.get(title);
    }

    @Override
    public List<Post> getMostCommented(){
        return postRepository.getMostCommented();
    }

    @Override
    public List<Post> getMostRecentlyCreated(){
        return postRepository.getMostRecentlyCreated();
    }

    @Override
    public Post create(Post post, User user) {

        boolean duplicateExists = true;
        try {
            postRepository.get(post.getTitle());
        } catch (EntityNotFoundException e) {
            duplicateExists = false;
        }

        if (duplicateExists) {
            throw new EntityDuplicateException("Post", "title", post.getTitle());
        }

        post.setUserId(user);

        postRepository.create(post);

        return postRepository.get(post.getTitle());
    }

    @Override
    public Post update(long postId, Post post, User user) {
        Post repoPost = postRepository.get(postId);
        authorizationHelper.checkOwnership(repoPost, user);

        boolean duplicateExists = true;
        try {
            Post existing = postRepository.get(post.getTitle());
            if (repoPost.getId() == existing.getId()) {
                duplicateExists = false;
            }
        } catch (EntityNotFoundException e) {
            duplicateExists = false;
        }

        if (duplicateExists) {
            throw new EntityDuplicateException("Post", "title", post.getTitle());
        }

        repoPost.setTitle(post.getTitle());
        repoPost.setContent(post.getContent());

        postRepository.update(repoPost);

        return postRepository.get(postId);
    }

    @Override
    public void delete(long id, User user) {
        Post post = postRepository.get(id);
        authorizationHelper.checkPermissionsAndOwnership(post,user,"ADMIN", "MODERATOR");

        postRepository.delete(post);
    }
}
