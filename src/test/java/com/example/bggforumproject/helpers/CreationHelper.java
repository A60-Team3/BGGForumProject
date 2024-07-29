package com.example.bggforumproject.helpers;

import com.example.bggforumproject.helpers.filters.CommentFilterOptions;
import com.example.bggforumproject.helpers.filters.PostFilterOptions;
import com.example.bggforumproject.helpers.filters.UserFilterOptions;
import com.example.bggforumproject.models.*;
import com.example.bggforumproject.models.enums.ReactionType;
import com.example.bggforumproject.models.enums.RoleType;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

public class CreationHelper {

    public static User createMockUser() {
        User user = new User();
        user.setId(1);
        user.setFirstName("first");
        user.setLastName("last");
        user.setEmail("email@email.com");
        user.setUsername("username");
        user.setPassword("password");
        user.setRegisteredAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        user.setBlocked(false);
        user.setDeleted(false);
        user.setRoles(new HashSet<>());
        user.getRoles().add(new Role(RoleType.USER.name()));
        return user;
    }

    public static User createMockModerator() {
        User user = createMockUser();
        user.getRoles().add(new Role(RoleType.MODERATOR.name()));
        return user;
    }

    public static User createMockAdmin() {
        User user = createMockUser();
        user.getRoles().add(new Role(RoleType.ADMIN.name()));
        return user;
    }

    public static Post createMockPost() {
        Post post = new Post();
        post.setUserId(createMockAdmin());
        post.setId(1);
        post.setTitle("imalo edno vreme na zapad");
        post.setContent("to ne bilo na zapad ami vuv dalechniq iztok ama e sushtoto");
        post.setCreatedAt(LocalDateTime.now());
        post.setUpdatedAt(LocalDateTime.now());

        return post;
    }

    public static Reaction createMockReaction() {
        Reaction reaction = new Reaction();
        reaction.setId(1);
        reaction.setReactionType(ReactionType.LIKE);
        reaction.setPostId(createMockPost());
        reaction.setUserId(createMockAdmin());
        return reaction;
    }

    public static Tag createMockTag() {
        Tag tag = new Tag();
        tag.setName("Zamunda Banana Bend");
        return tag;
    }

    public static Post createMockPostWithTags() {
        Tag tag = createMockTag();
        Post post = createMockPost();
        post.setTags(Set.of(tag));
        tag.setPosts(Set.of(post));

        return post;
    }

    public static Comment createMockComment() {
        Comment comment = new Comment();
        comment.setId(1);
        comment.setContent("imalo edno vreme na zapad");
        comment.setCreatedAt(LocalDateTime.now());
        comment.setUpdatedAt(LocalDateTime.now());
        comment.setPostId(createMockPost());
        comment.setUserId(createMockUser());
        return comment;
    }

    public static Tag createTagWithPosts() {
        Tag tag = createMockTag();
        Post post = createMockPost();
        post.setTags(Set.of(tag));
        tag.setPosts(Set.of(post));

        return tag;
    }

    public static UserFilterOptions createMockUserFilterOptions() {
        return new UserFilterOptions(
                "first",
                "last",
                "email@email.com",
                "username",
                null,
                null,
                false,
                false,
                null, null, null);
    }

    public static PostFilterOptions createMockPostFilterOptions() {
        return new PostFilterOptions(
                "imalo edno vreme na zapad",
                "to ne bilo na zapad ami vuv dalechniq iztok ama e sushtoto",
                1L,
                null,
                null,
                null,
                null,
                null
        );
    }

    public static CommentFilterOptions createMockCommentFilterOptions() {
        return new CommentFilterOptions(
                "imalo edno vreme na zapad",
                null,
                null,
                1L,
                1L,
                null,
                null
        );
    }
}

