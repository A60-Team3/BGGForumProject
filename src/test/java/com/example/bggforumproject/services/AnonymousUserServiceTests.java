package com.example.bggforumproject.services;

import com.example.bggforumproject.models.Post;
import com.example.bggforumproject.models.User;
import com.example.bggforumproject.repositories.contracts.PostRepository;
import com.example.bggforumproject.repositories.contracts.UserRepository;
import com.example.bggforumproject.helpers.filters.PostFilterOptions;
import com.example.bggforumproject.service.AnonymousUserServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;

import java.util.List;

import static com.example.bggforumproject.helpers.CreationHelper.*;
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
        List<User> userEntities = List.of(createMockAdmin(), createMockModerator(), createMockUser());

        Mockito.when(userRepository.getAll()).thenReturn(userEntities);

        assertEquals(3, anonymousUserService.countUsers());
    }

    @Test
    public void countUsers_Should_CallRepositoryAndReturnUserCount() {
        List<User> userEntities = List.of(createMockAdmin(), createMockModerator(), createMockUser());

        Mockito.when(userRepository.getAll()).thenReturn(userEntities);

        long countUsers = anonymousUserService.countUsers();

        assertEquals(3, countUsers);
        Mockito.verify(userRepository, Mockito.times(1)).getAll();
    }

    @Test
    public void countPosts_Should_CallRepositoryAndReturnCountPosts() {
        Post post = createMockPostWithTags();
        List<Post> posts = List.of(post);

        Mockito.when(postRepository.get()).thenReturn(posts);

        long countPosts = anonymousUserService.countPosts();

        assertEquals(1, countPosts);
        Mockito.verify(postRepository, Mockito.times(1)).get();
    }

    @Test
    public void getAllPosts_Should_Return_All_Posts_In_Database_With_No_Filters() {
        Post post = createMockPostWithTags();
        PostFilterOptions postFilterOptions = Mockito.mock(PostFilterOptions.class);

        List<Post> posts = List.of(post);
        int pageIndex = 0;
        int pageSize = 0;

        Mockito.when(postRepository.get(postFilterOptions, pageIndex, pageSize))
                .thenReturn(new PageImpl<>(posts));

        assertEquals(posts, anonymousUserService.getAllPosts(postFilterOptions, pageIndex, pageSize));
        Mockito.verify(postRepository, Mockito.times(1)).get(postFilterOptions, pageIndex, pageSize);

    }

    @Test
    public void getAllPosts_Should_Return_No_Posts_When__Filters_Mismatch() {
        PostFilterOptions postFilterOptions =
                new PostFilterOptions(
                        "microsoft", null, null, null, null, null,
                        null, null, null, null, null
                );

        int pageIndex = 0;
        int pageSize = 0;

        Mockito.when(postRepository.get(postFilterOptions, pageIndex, pageSize))
                .thenReturn(new PageImpl<>(List.of()));

        assertEquals(0, anonymousUserService.getAllPosts(postFilterOptions, pageIndex, pageSize).getTotalElements());
        Mockito.verify(postRepository, Mockito.times(1)).get(postFilterOptions, pageIndex, pageSize);

    }

}
