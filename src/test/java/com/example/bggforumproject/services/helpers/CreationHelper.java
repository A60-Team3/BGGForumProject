package com.example.bggforumproject.services.helpers;

import com.example.bggforumproject.models.Post;
import com.example.bggforumproject.models.Role;
import com.example.bggforumproject.models.Tag;
import com.example.bggforumproject.models.User;
import com.example.bggforumproject.models.enums.RoleType;

import java.time.LocalDateTime;
import java.util.Set;

public class CreationHelper {

    public static User createUser() {
        User user = new User();
        user.setId(1);
        user.setLastName("last");
        user.setFirstName("first");
        user.setEmail("email@email.com");
        user.setUsername("username");
        user.setPassword("password");
        user.setRegisteredAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        user.setBlocked(false);
        user.setDeleted(false);
        user.setRoles(Set.of(new Role(RoleType.USER.name())));
        return user;
    }

    public static User createModerator(){
        User user = createUser();
        user.setRoles(Set.of(new Role(RoleType.MODERATOR.name())));
        return user;
    }

    public static User createAdmin(){
        User user = createUser();
        user.setRoles(Set.of(new Role(RoleType.ADMIN.name())));
        return user;
    }

    public static Post createPost(){
        Post post = new Post();
        post.setUserId(createAdmin());
        post.setId(1);
        post.setTitle("imalo edno vreme na zapad");
        post.setContent("to ne bilo na zapad ami vuv dalechniq iztok ama e sushtoto");
        post.setCreatedAt(LocalDateTime.now());
        post.setUpdatedAt(LocalDateTime.now());

        return post;
    }

    public static Tag createTag(){
        Tag tag = new Tag();
        tag.setName("Zamunda Banana Bend");
        return tag;
    }

    public static Post createPostWithTags(){
        Tag tag = createTag();
        Post post = createPost();
        post.setTags(Set.of(tag));
        tag.setPosts(Set.of(post));

        return post;
    }

    public static Tag createTagWithPosts(){
        Tag tag = createTag();
        Post post = createPost();
        post.setTags(Set.of(tag));
        tag.setPosts(Set.of(post));

        return tag;
    }
}
