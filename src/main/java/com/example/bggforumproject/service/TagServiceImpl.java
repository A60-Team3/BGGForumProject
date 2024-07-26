package com.example.bggforumproject.service;

import com.example.bggforumproject.helpers.AuthorizationHelper;
import com.example.bggforumproject.models.Post;
import com.example.bggforumproject.models.Tag;
import com.example.bggforumproject.models.User;
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

    private static final String DELETE_TAG_ERROR_MESSAGE = "Comments can only be deleted by the post creator, moderators or admins!";

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
    public List<Tag> get() {
        return tagRepository.get();
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
    public void addTagToPost(Tag tag, Post post) {

        try {
            tagRepository.get(tag.getName());
        } catch (EntityNotFoundException e) {
            create(tag);
        }

        Tag tagToAdd = tagRepository.get(tag.getName());
        if (!post.getTags().contains(tagToAdd)) {
            post.getTags().add(tagToAdd);
            postRepository.update(post);
        } else {
            throw new EntityDuplicateException("Tag", "name", tagToAdd.getName());
        }
    }

    @Override
    public void deleteTagFromPost(long tagId, Post post, User user) {
        try {
            authorizationHelper.checkPermissionsAndOwnership(post.getId(), user, postRepository, "ADMIN", "MODERATOR");
        } catch (AuthorizationException e) {
            throw new AuthorizationException(DELETE_TAG_ERROR_MESSAGE);
        }

        Tag tag = tagRepository.get(tagId);
        if (post.getTags().contains(tag)) {
            post.getTags().remove(tag);
            postRepository.update(post);
        } else {
            throw new EntityNotFoundException("Tag", "name", tag.getName());
        }

    }

    /*@Override
    public List<Tag> getTagsOfPost(long postId) {
        return tagRepository.getTagsOfPost(postId);
    }*/

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
    public void update(Tag tag, User user) {
        //TODO if implement check for ownership, must receive postID but not from tag
        boolean hasPermission = true;
        try {
            authorizationHelper.checkPermissions(user, "ADMIN", "MODERATOR");
        } catch (AuthorizationException e) {
            hasPermission = false;
        }

        if (!hasPermission) {
            try {
                authorizationHelper.checkOwnership(tag.getId(), user, postRepository);
            } catch (AuthorizationException e) {
                throw new AuthorizationException(DELETE_TAG_ERROR_MESSAGE);
            }
        }

        boolean isDuplicate = true;

        try {
            Tag existingTag = tagRepository.get(tag.getName());
            if (existingTag.getId() == tag.getId()) {
                isDuplicate = false;
            }
        } catch (EntityNotFoundException e) {
            isDuplicate = false;
        }

        if (isDuplicate) {
            throw new EntityDuplicateException("Tag", "name", tag.getName());
        }

        tagRepository.update(tag);
    }

    @Override
    public void delete(long id, User user) {
        authorizationHelper.checkPermissions(user, "ADMIN", "MODERATOR");
        tagRepository.delete(id);
    }
}
