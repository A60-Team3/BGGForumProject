package com.example.bggforumproject.services;

import com.example.bggforumproject.exceptions.AuthorizationException;
import com.example.bggforumproject.exceptions.EntityDuplicateException;
import com.example.bggforumproject.exceptions.EntityNotFoundException;
import com.example.bggforumproject.exceptions.PostMismatchException;
import com.example.bggforumproject.helpers.AuthorizationHelper;
import com.example.bggforumproject.helpers.filters.TagFilterOptions;
import com.example.bggforumproject.models.Post;
import com.example.bggforumproject.models.Role;
import com.example.bggforumproject.models.Tag;
import com.example.bggforumproject.models.User;
import com.example.bggforumproject.repositories.contracts.PostRepository;
import com.example.bggforumproject.repositories.contracts.TagRepository;
import com.example.bggforumproject.service.TagServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.Set;

import static com.example.bggforumproject.helpers.CreationHelper.*;

@ExtendWith(MockitoExtension.class)
public class TagServiceTests {

    @InjectMocks
    private TagServiceImpl tagService;

    @Mock
    private TagRepository tagRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private AuthorizationHelper authorizationHelper;

    @Test
    public void get_Should_CallRepository() {
        Tag tag = createMockTag();

        TagFilterOptions filter = null;
        tagService.get(filter);

        Mockito.verify(tagRepository, Mockito.times(1))
                .get(filter);
    }

    @Test
    public void get_Should_CallRepository_When_IdIsProvided() {
        tagService.get(1);

        Mockito.verify(tagRepository, Mockito.times(1))
                .get(1);
    }

    @Test
    public void get_Should_CallRepository_When_NameIsProvided() {
        tagService.get(Mockito.anyString());

        Mockito.verify(tagRepository, Mockito.times(1))
                .get(Mockito.anyString());
    }

    @Test
    public void get_Should_ReturnTag_When_IdIsProvided() {
        Tag tag = createMockTag();

        Mockito.when(tagRepository.get(tag.getId())).thenReturn(tag);
        Tag result = tagService.get(tag.getId());

        Assertions.assertSame(tag, result);
    }

    @Test
    public void get_Should_ReturnTag_When_NameIsProvided() {
        Tag tag = createMockTag();

        Mockito.when(tagRepository.get(tag.getName())).thenReturn(tag);
        Tag result = tagService.get(tag.getName());

        Assertions.assertSame(tag, result);
    }

    @Test
    public void addTagToPost_Should_AddTagToPost_When_PostHasNoSuchTag() {
        User user = createMockUser();
        Tag tag = createMockTag();
        Tag tag2 = createMockTag();
        tag2.setName("mock tag");
        tag2.setId(2);
        Post post = createMockPost();
        Set<Tag> tags = new HashSet<>();
        tags.add(tag);
        post.setTags(tags);
        post.setUserId(user);

        Mockito.when(tagRepository.get(tag2.getName())).thenThrow(EntityNotFoundException.class);
        tagService.addTagToPost(post.getId(), tag2.getName(), user);

        Mockito.verify(postRepository, Mockito.times(1))
                .update(post);

    }

    @Test
    public void addTagToPost_Should_Throw_When_PostAlreadyHasSameTag() {
        User user = createMockUser();
        Tag tag = createMockTag();
        Post post = createMockPost();

        Mockito.when(tagRepository.get(tag.getName())).thenReturn(tag);
        Set<Tag> tags = new HashSet<>();
        tags.add(tag);
        post.setTags(tags);
        post.setUserId(user);

        Assertions.assertThrows(EntityDuplicateException.class,
                () -> tagService.addTagToPost(post.getId(), tag.getName(), user));
    }

    @Test
    public void deleteTagFromPost_Should_RemoveTagFromPost_When_UserIsPostCreator() {
        Tag tag = createMockTag();
        Post post = createMockPost();
        Set<Post> posts = new HashSet<>();
        posts.add(post);
        tag.setPosts(posts);
        User user = createMockUser();
        post.setUserId(user);
        Set<Tag> tags = new HashSet<>();
        tags.add(tag);
        post.setTags(tags);

        Mockito.when(tagRepository.get(tag.getId())).thenReturn(tag);
        Mockito
                .doNothing()
                .when(authorizationHelper)
                .checkPermissionsAndOwnership(post, user, "ADMIN", "MODERATOR");

        tagService.deleteTagFromPost(tag.getId(), post.getId(), user);
        Mockito.verify(postRepository, Mockito.times(1))
                .update(post);
    }

    @Test
    public void deleteTagFromPost_Should_Throw_When_UserIsNotPostCreator() {
        Tag tag = createMockTag();
        Post post = createMockPost();
        Set<Post> posts = new HashSet<>();
        posts.add(post);
        tag.setPosts(posts);
        User user = createMockUser();
        User notCreator = createMockUser();
        notCreator.setId(2);
        notCreator.setUsername("notcreator");
        post.setUserId(user);
        Set<Tag> tags = new HashSet<>();
        tags.add(tag);
        post.setTags(tags);

        Mockito.when(tagRepository.get(tag.getId())).thenReturn(tag);
        Mockito
                .doThrow(AuthorizationException.class)
                .when(authorizationHelper)
                .checkPermissionsAndOwnership(post, notCreator, "ADMIN", "MODERATOR");

        Assertions.assertThrows(AuthorizationException.class,
                () -> tagService.deleteTagFromPost(tag.getId(), post.getId(), notCreator));
    }

    @Test
    public void deleteTagFromPost_Should_Throw_When_PostHasNoSuchTag() {
        Tag tag = createMockTag();
        Post post = createMockPost();
        User user = createMockUser();
        post.setUserId(user);
        Set<Tag> tags = new HashSet<>();
        post.setTags(tags);


        Mockito.when(tagRepository.get(tag.getId())).thenThrow(PostMismatchException.class);

        Assertions.assertThrows(PostMismatchException.class,
                () -> tagService.deleteTagFromPost(tag.getId(), post.getId(), user));
    }

    @Test
    public void create_Should_CreateTag_When_NotDuplicate() {
        Tag tag = createMockTag();

        Mockito.when(tagRepository.get(tag.getName())).thenThrow(EntityNotFoundException.class);

        tagService.create(tag);
        Mockito.verify(tagRepository, Mockito.times(1))
                .create(tag);
    }

    @Test
    public void create_Should_Throw_When_DuplicateExists() {
        Tag tag = createMockTag();

        Mockito.when(tagRepository.get(tag.getName())).thenReturn(tag);

        Assertions.assertThrows(EntityDuplicateException.class,
                () -> tagService.create(tag));
    }

    @Test
    public void delete_Should_DeleteTag_When_UserIsAdminOrModerator() {
        Tag tag = createMockTag();
        User user = createMockUser();
        Mockito
                .doNothing()
                .when(authorizationHelper)
                .checkPermissions(user, "ADMIN", "MODERATOR");

        tagService.delete(tag.getId(), user);

        Mockito.verify(tagRepository, Mockito.times(1))
                .delete(tag);
    }

    @Test
    public void delete_Should_Throw_When_UserIsRegularUser() {
        Tag tag = createMockTag();
        User user = createMockUser();
        Role role = new Role("USER");
        Set<Role> roles = new HashSet<>();
        roles.add(role);
        user.setRoles(roles);
        Mockito
                .doThrow(AuthorizationException.class)
                .when(authorizationHelper)
                .checkPermissions(user, "ADMIN", "MODERATOR");

        Assertions.assertThrows(AuthorizationException.class,
                () -> tagService.delete(tag.getId(), user));
    }
}
