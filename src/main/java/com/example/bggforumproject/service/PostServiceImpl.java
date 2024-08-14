package com.example.bggforumproject.service;

import com.example.bggforumproject.exceptions.EntityDuplicateException;
import com.example.bggforumproject.exceptions.EntityNotFoundException;
import com.example.bggforumproject.helpers.AuthorizationHelper;
import com.example.bggforumproject.helpers.filters.PostFilterOptions;
import com.example.bggforumproject.models.Post;
import com.example.bggforumproject.models.User;
import com.example.bggforumproject.models.enums.RoleType;
import com.example.bggforumproject.repositories.contracts.PostRepository;
import com.example.bggforumproject.service.contacts.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final AuthorizationHelper authorizationHelper;

    @Autowired
    public PostServiceImpl(PostRepository postRepository, AuthorizationHelper authorizationHelper) {
        this.postRepository = postRepository;
        this.authorizationHelper = authorizationHelper;
    }

    @Override
    public Page<Post> get(PostFilterOptions postFilterOptions, int pageIndex, int pageSize) {
        return postRepository.get(postFilterOptions, pageIndex, pageSize);
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
    public List<Post> getMostCommented() {
        return postRepository.getMostCommented();
    }

    @Override
    public Page<Post> getMostRecentlyCreated(int pageIndex, int pageSize) {
        return postRepository.getMostRecentlyCreated(pageIndex, pageSize);
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
        authorizationHelper.checkPermissionsAndOwnership(post, user, RoleType.ADMIN, RoleType.MODERATOR);

        postRepository.delete(post);
    }
}
