package com.example.bggforumproject.services;

import com.example.bggforumproject.exceptions.AuthorizationException;
import com.example.bggforumproject.helpers.AuthorizationHelper;
import com.example.bggforumproject.helpers.filters.CommentFilterOptions;
import com.example.bggforumproject.models.Comment;
import com.example.bggforumproject.models.Post;
import com.example.bggforumproject.models.User;
import com.example.bggforumproject.models.enums.RoleType;
import com.example.bggforumproject.repositories.contracts.CommentRepository;
import com.example.bggforumproject.repositories.contracts.PostRepository;
import com.example.bggforumproject.service.CommentServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.example.bggforumproject.helpers.CreationHelper.*;

@ExtendWith(MockitoExtension.class)
public class CommentServiceTests {

    @InjectMocks
    private CommentServiceImpl commentService;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private AuthorizationHelper authorizationHelper;

    @Test
    public void getCommentsForPost_Should_CallRepository() {
        Post post = createMockPost();
        CommentFilterOptions mockCommentFilterOptions = createMockCommentFilterOptions();

        int pageIndex = 0;
        int pageSize = 10;

        Mockito.when(postRepository.get(Mockito.anyLong())).thenReturn(post);

        commentService.getCommentsForPost(post.getId(), pageIndex, pageSize);

        Mockito.verify(commentRepository, Mockito.times(1))
                .getCommentsForPost(post.getId(), pageIndex, pageSize);
    }

    @Test
    public void get_Should_CallRepository() {
        Comment comment = createMockComment();

        Mockito.when(commentRepository.get(comment.getId())).thenReturn(comment);
        commentService.get(comment.getId());

        Mockito.verify(commentRepository, Mockito.times(1))
                .get(comment.getId());
    }

    @Test
    public void get_Should_ReturnComment_When_CommentExists() {
        Comment comment = createMockComment();

        Mockito.when(commentRepository.get(comment.getId()))
                .thenReturn(comment);

        Comment result = commentService.get(comment.getId());

        Assertions.assertSame(comment, result);
    }

    @Test
    public void create_Should_CreateComment_When_Valid() {
        Post post = createMockPost();
        Comment comment = createMockComment();
        User user = createMockUser();

        Mockito.when(postRepository.get(Mockito.anyLong()))
                .thenReturn(post);

        commentService.create(post.getId(), comment, user);

        Mockito.verify(commentRepository, Mockito.times(1))
                .create(comment);
    }

    @Test
    public void update_Should_UpdateComment_When_UserIsCreator() {
        Comment comment = createMockComment();
        User user = createMockUser();
        Post post = createMockPost();


        comment.setUserId(user);

        Mockito.when(postRepository.get(Mockito.anyLong()))
                .thenReturn(post);

        Mockito.when(commentRepository.get(Mockito.anyLong()))
                .thenReturn(comment);

        Mockito
                .doNothing()
                .when(authorizationHelper)
                .checkOwnership(comment, user);

        commentService.update(comment.getId(), post.getId(), comment.getContent(), user);

        Mockito.verify(commentRepository, Mockito.times(1))
                .update(comment);
    }

    @Test
    public void update_Should_Throw_When_UserIsNotCreator() {
        Comment comment = createMockComment();
        Post post = createMockPost();
        User user = createMockUser();
        User notCreator = createMockUser();
        notCreator.setId(2);
        notCreator.setUsername("notcreator");
        comment.setUserId(user);

        Mockito
                .doThrow(AuthorizationException.class)
                .when(authorizationHelper)
                .checkOwnership(comment, notCreator);

        Assertions.assertThrows(AuthorizationException.class,
                () -> commentService.update(comment.getId(), post.getId(), comment.getContent(), notCreator));
    }

    @Test
    public void delete_Should_DeleteComment_When_UserIsCreatorOrAdminOrModerator() {
        Comment comment = createMockComment();
        Post post = createMockPost();
        User user = createMockUser();

        comment.setUserId(user);

        Mockito
                .doNothing()
                .when(authorizationHelper)
                .checkPermissionsAndOwnership(comment, user, RoleType.ADMIN, RoleType.MODERATOR);

        commentService.delete(comment.getId(), post.getId(), user);

        Mockito.verify(commentRepository, Mockito.times(1))
                .delete(comment.getId());
    }

    @Test
    public void delete_Should_Throw_When_UserIsNotCreatorOrAdminOrModerator() {
        Comment comment = createMockComment();
        Post post = createMockPost();
        User user = createMockUser();
        User notCreator = createMockUser();
        notCreator.setId(2);
        notCreator.setUsername("notcreator");
        comment.setUserId(user);

        Mockito
                .doThrow(AuthorizationException.class)
                .when(authorizationHelper)
                .checkPermissionsAndOwnership(comment, notCreator, RoleType.ADMIN, RoleType.MODERATOR);

        Assertions.assertThrows(AuthorizationException.class,
                () -> commentService.delete(comment.getId(), post.getId(), notCreator));
    }
}
