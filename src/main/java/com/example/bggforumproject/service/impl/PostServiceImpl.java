package com.example.bggforumproject.service.impl;

import com.example.bggforumproject.persistance.models.Post;
import com.example.bggforumproject.persistance.models.Role;
import com.example.bggforumproject.persistance.models.User;
import com.example.bggforumproject.persistance.repositories.PostRepository;
import com.example.bggforumproject.presentation.exceptions.AuthorizationException;
import com.example.bggforumproject.presentation.exceptions.EntityNotFoundException;
import com.example.bggforumproject.presentation.helpers.PostFilterOptions;
import com.example.bggforumproject.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PostServiceImpl implements PostService {

    private static final String MODIFY_POST_ERROR_MESSAGE = "Posts can only be modified by their creator!";
    private static final String DELETE_POST_ERROR_MESSAGE = "Posts can only be deleted by their creator, moderators or admins!";

    private final PostRepository postRepository;

    @Autowired
    public PostServiceImpl(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    @Override
    public List<Post> get(PostFilterOptions postFilterOptions) {
        return postRepository.get(postFilterOptions);
    }

    @Override
    public Post get(int id) {
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
    public void create(Post post, User user) {

        post.setUserId(user);
        postRepository.create(post);
    }

    @Override
    public void update(Post post, User user) throws EntityNotFoundException{
        checkModifyPermissions(post.getId(), user);


        postRepository.update(post);
    }

    @Override
    public void delete(int id, User user) {
        checkDeletePermissions(id, user);

        postRepository.delete(id);
    }

    private void checkModifyPermissions(long postId, User user) {
        Post post = postRepository.get((int) postId);
        if (post.getUserId().getId() != user.getId()) {
            throw new AuthorizationException(MODIFY_POST_ERROR_MESSAGE);
        }
    }

    private void checkDeletePermissions(long postId, User user) {
        Post post = postRepository.get((int) postId);
        boolean isRegularUser = user.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("USER"));
        if (isRegularUser) {
            if(user.getId() != post.getUserId().getId()) {
                throw new AuthorizationException(DELETE_POST_ERROR_MESSAGE);
            }
        }
    }
}
