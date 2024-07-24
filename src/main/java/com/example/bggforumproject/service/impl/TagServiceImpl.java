package com.example.bggforumproject.service.impl;

import com.example.bggforumproject.persistance.models.Post;
import com.example.bggforumproject.persistance.models.Tag;
import com.example.bggforumproject.persistance.models.User;
import com.example.bggforumproject.persistance.repositories.PostRepository;
import com.example.bggforumproject.persistance.repositories.TagRepository;
import com.example.bggforumproject.presentation.exceptions.AuthorizationException;
import com.example.bggforumproject.presentation.exceptions.EntityDuplicateException;
import com.example.bggforumproject.presentation.exceptions.EntityNotFoundException;
import com.example.bggforumproject.service.TagService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TagServiceImpl implements TagService {

    private static final String MODIFY_TAG_ERROR_MESSAGE = "Tags can only be deleted by admins or moderators (for now)!";

    private final TagRepository tagRepository;
    private final PostRepository postRepository;

    @Autowired
    public TagServiceImpl(TagRepository tagRepository, PostRepository postRepository) {
        this.tagRepository = tagRepository;
        this.postRepository = postRepository;
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
        } else{
            throw new EntityDuplicateException("Tag", "name", tagToAdd.getName());
        }
    }

    @Override
    public void deleteTagFromPost(long tagId, Post post, User user) {
        checkModifyPermissions(user);

        Tag tag = tagRepository.get(tagId);
        if(post.getTags().contains(tag)){
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

        checkModifyPermissions(user);

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
        checkModifyPermissions(user);

        tagRepository.delete(id);
    }

    private void checkModifyPermissions(User user) {

        boolean isRegularUser = user.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("USER"));
        if (isRegularUser) {
            throw new AuthorizationException(MODIFY_TAG_ERROR_MESSAGE);
        }
    }
}
