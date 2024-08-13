package com.example.bggforumproject.services;

import com.example.bggforumproject.exceptions.AuthorizationException;
import com.example.bggforumproject.helpers.filters.PostFilterOptions;
import com.example.bggforumproject.models.Post;
import com.example.bggforumproject.models.User;
import com.example.bggforumproject.repositories.contracts.PostRepository;
import com.example.bggforumproject.service.PostServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.example.bggforumproject.helpers.CreationHelper.*;

@ExtendWith(MockitoExtension.class)
public class PostServiceTests {

    @InjectMocks
    private PostServiceImpl postService;

    @Mock
    private PostRepository postRepository;

    @Test
    public void get_Should_CallRepository(){
        PostFilterOptions mockPostFilterOptions = createMockPostFilterOptions();
        int pageIndex = 0;
        int pageSize = 10;

        Mockito.when(postRepository.get(mockPostFilterOptions, pageIndex, pageSize))
                .thenReturn(null);

        postService.get(mockPostFilterOptions, pageIndex, pageSize);

        Mockito.verify(postRepository, Mockito.times(1))
                .get(mockPostFilterOptions, pageIndex, pageSize);
    }

    @Test
    public void get_Should_CallRepository_When_IdIsProvided(){
        postService.get(1);

        Mockito.verify(postRepository, Mockito.times(1))
                .get(1);
    }

    @Test
    public void get_Should_CallRepository_When_TitleIsProvided(){
        postService.get(Mockito.anyString());

        Mockito.verify(postRepository, Mockito.times(1))
                .get(Mockito.anyString());
    }

    @Test
    public void get_Should_ReturnPost_When_IdIsProvided(){
        Post post = createMockPost();

        Mockito.when(postRepository.get(post.getId()))
                .thenReturn(post);

        Post result = postService.get(post.getId());

        Assertions.assertSame(post, result);
    }

    @Test
    public void get_Should_ReturnPost_When_TitleIsProvided(){
        Post post = createMockPost();

        Mockito.when(postRepository.get(post.getTitle()))
                .thenReturn(post);

        Post result = postService.get(post.getTitle());

        Assertions.assertSame(post, result);
    }

    @Test
    public void getMostCommented_Should_CallRepository(){
        postService.getMostCommented();

        Mockito.verify(postRepository, Mockito.times(1))
                .getMostCommented();
    }

    @Test
    public void getMostRecentlyCreated_Should_CallRepository(){
        postService.getMostRecentlyCreated();

        Mockito.verify(postRepository, Mockito.times(1))
                .getMostRecentlyCreated();
    }

    @Test
    public void create_Should_CreatePost_When_Valid(){
        Post post = createMockPost();
        User user = createMockUser();

        postService.create(post, user);

        Mockito.verify(postRepository, Mockito.times(1))
                .create(Mockito.any(Post.class));
    }

    @Test
    public void update_Should_UpdatePost_When_UserIsCreator(){
        Post post = createMockPost();
        User user = createMockUser();

        post.setUserId(user);

        Mockito.when(postRepository.get(1)).thenReturn(post);

        postService.update(post.getId(), post, user);
        Mockito.verify(postRepository, Mockito.times(1))
                .update(post);
    }

    @Test
    public void update_Should_Throw_When_UserIsNotCreator(){
        Post post = createMockPost();
        User user = createMockUser();
        User notCreator = createMockUser();
        notCreator.setId(2);
        notCreator.setUsername("notcreator");
        post.setUserId(user);

        Mockito.when(postRepository.get(1)).thenReturn(post);

        Assertions.assertThrows(AuthorizationException.class,
                () -> postService.update(post.getId(), post, notCreator));
    }

    @Test
    public void delete_Should_DeletePost_When_UserIsCreator(){
        Post post = createMockPost();
        User user = createMockUser();
        post.setUserId(user);

        Mockito.when(postRepository.get(1)).thenReturn(post);

        postService.delete(post.getId(), user);

        Mockito.verify(postRepository, Mockito.times(1))
                .delete(post);
    }

    @Test
    public void delete_Should_DeletePost_When_UserIsAdmin(){
        Post post = createMockPost();
        User user = createMockUser();
        post.setUserId(user);
        User admin = createMockAdmin();

        Mockito.when(postRepository.get(1)).thenReturn(post);

        postService.delete(post.getId(), admin);

        Mockito.verify(postRepository, Mockito.times(1))
                .delete(post);
    }

    @Test
    public void delete_Should_DeletePost_When_UserIsModerator(){
        Post post = createMockPost();
        User user = createMockUser();
        post.setUserId(user);
        User moderator = createMockModerator();

        Mockito.when(postRepository.get(1)).thenReturn(post);

        postService.delete(post.getId(), moderator);

        Mockito.verify(postRepository, Mockito.times(1))
                .delete(post);
    }

    @Test
    public void delete_Should_Throw_When_UserIsNotOwnerOrAdminOrModerator(){
        Post post = createMockPost();
        User user = createMockUser();
        User notCreator = createMockUser();
        notCreator.setId(2);
        notCreator.setUsername("notcreator");
        post.setUserId(user);

        Mockito.when(postRepository.get(1)).thenReturn(post);

        Assertions.assertThrows(AuthorizationException.class,
                () -> postService.delete(post.getId(), notCreator));
    }
}
