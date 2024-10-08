package com.example.bggforumproject.service;

import com.example.bggforumproject.exceptions.PostMismatchException;
import com.example.bggforumproject.helpers.AuthorizationHelper;
import com.example.bggforumproject.helpers.filters.TagFilterOptions;
import com.example.bggforumproject.models.Post;
import com.example.bggforumproject.models.Tag;
import com.example.bggforumproject.models.User;
import com.example.bggforumproject.models.enums.RoleType;
import com.example.bggforumproject.repositories.contracts.PostRepository;
import com.example.bggforumproject.repositories.contracts.TagRepository;
import com.example.bggforumproject.exceptions.AuthorizationException;
import com.example.bggforumproject.exceptions.EntityDuplicateException;
import com.example.bggforumproject.exceptions.EntityNotFoundException;
import com.example.bggforumproject.service.contacts.TagService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TagServiceImpl implements TagService {

    private static final String DELETE_TAG_ERROR_MESSAGE = "Tags can only be deleted by the post creator, moderators or admins!";

    private final TagRepository tagRepository;
    private final PostRepository postRepository;
    private final AuthorizationHelper authorizationHelper;

    @Autowired
    public TagServiceImpl(TagRepository tagRepository, PostRepository postRepository, AuthorizationHelper authorizationHelper) {
        this.tagRepository = tagRepository;
        this.postRepository = postRepository;
        this.authorizationHelper = authorizationHelper;
    }

    @Override
    public List<Tag> get(TagFilterOptions tagFilterOptions) {
        return tagRepository.get(tagFilterOptions);
    }

    @Override
    public Tag get(long id) {
        return tagRepository.get(id);
    }

    @Override
    public Tag get(String name) {
        return tagRepository.get(name);
    }

    @Override
    @Transactional
    public Post addTagToPost(long id, String tagName, User user) {

        Post post = postRepository.get(id);

        authorizationHelper.checkOwnership(post, user);

        try {
            tagRepository.get(tagName);
        } catch (EntityNotFoundException e) {
            Tag tag = new Tag();
            tag.setName(tagName);
            tagRepository.create(tag);
        }

        Tag persistedTag = tagRepository.get(tagName);
        if (!post.getTags().contains(persistedTag)) {
            post.getTags().add(persistedTag);
            postRepository.update(post);
        } else {
            throw new EntityDuplicateException("Tag", "name", tagName);
        }

        return postRepository.get(id);
    }

    @Override
    public void deleteTagFromPost(long tagId, long postId, User user) {
        Tag tag = tagRepository.get(tagId);

        Post post = tag.getPosts().stream()
                .filter(p -> p.getId() == postId)
                .findFirst()
                .orElseThrow(() -> new PostMismatchException("tag",tag.getName()));
        try {
            authorizationHelper.checkPermissionsAndOwnership(post, user,  RoleType.ADMIN,RoleType.MODERATOR);
        } catch (AuthorizationException e) {
            throw new AuthorizationException(DELETE_TAG_ERROR_MESSAGE);
        }

        // TODO remove bidirectional, rework this stuff here

        if (post.getTags().contains(tag)) {
            post.getTags().remove(tag);
            postRepository.update(post);
        } else {
            throw new PostMismatchException("Tag", tag.getName());
        }

    }

    @Override
    public void create(Tag tag) {
        boolean isDuplicate = true;

        try {
            tagRepository.get(tag.getName());
        } catch (EntityNotFoundException e) {
            isDuplicate = false;
        }

        if (isDuplicate) {
            throw new EntityDuplicateException("Tag", "name", tag.getName());
        }

        tagRepository.create(tag);
    }

    @Override
    public void delete(long id, User user) {
        authorizationHelper.checkPermissions(user, RoleType.ADMIN,RoleType.MODERATOR);
        Tag tagToDelete = tagRepository.get(id);
        tagRepository.delete(tagToDelete);
    }
}