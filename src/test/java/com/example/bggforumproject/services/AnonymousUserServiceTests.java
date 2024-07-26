package com.example.bggforumproject.services;

import com.example.bggforumproject.models.Post;
import com.example.bggforumproject.models.User;
import com.example.bggforumproject.repositories.contracts.PostRepository;
import com.example.bggforumproject.repositories.contracts.UserRepository;
import com.example.bggforumproject.helpers.PostFilterOptions;
import com.example.bggforumproject.service.AnonymousUserServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static com.example.bggforumproject.services.helpers.CreationHelper.*;
import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(MockitoExtension.class)
public class AnonymousUserServiceTests {

    @InjectMocks
    private AnonymousUserServiceImpl anonymousUserService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PostRepository postRepository;

    @Test
    public void countUsers_Should_Return_CountUsersInDatabase() {
        List<User> users = List.of(createAdmin(), createModerator(), createUser());

        Mockito.when(userRepository.findAll()).thenReturn(users);

        assertEquals(3, anonymousUserService.countUsers());
    }

    @Test
    public void countPosts_Should_Return_CountPostsInDatabase() {
        Post post = createPostWithTags();

        List<Post> posts = List.of(post);

        Mockito.when(postRepository.get()).thenReturn(posts);

        assertEquals(1, anonymousUserService.countPosts());
    }

    @Test
    public void getAllPosts_Should_Return_All_Posts_In_Database_With_No_Filters() {
        Post post = createPostWithTags();
        PostFilterOptions postFilterOptions = Mockito.mock(PostFilterOptions.class);

        List<Post> posts = List.of(post);

        Mockito.when(postRepository.get(postFilterOptions)).thenReturn(posts);

        assertEquals(posts, anonymousUserService.getAllPosts(postFilterOptions));
    }

    @Test
    public void getAllPosts_Should_Return_No_Posts_When__Filters_Mismatch() {
        PostFilterOptions postFilterOptions =
                new PostFilterOptions(
                        "microsoft", null, null, null,
                        null, null, null, null
                );

        Mockito.when(postRepository.get(postFilterOptions)).thenReturn(List.of());

        assertEquals(0, anonymousUserService.getAllPosts(postFilterOptions).size());
    }


}
