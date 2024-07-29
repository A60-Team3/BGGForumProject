package com.example.bggforumproject.services;

import com.example.bggforumproject.exceptions.AuthorizationException;
import com.example.bggforumproject.exceptions.EntityNotFoundException;
import com.example.bggforumproject.helpers.AuthorizationHelper;
import com.example.bggforumproject.models.Comment;
import com.example.bggforumproject.models.Post;
import com.example.bggforumproject.models.User;
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

        Mockito.when(postRepository.get(post.getId())).thenReturn(post);
        commentService.getCommentsForPost(post.getId());

        Mockito.verify(commentRepository, Mockito.times(1))
                .getCommentsForPost(post.getId());
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
        Comment comment = createMockComment();
        User user = createMockUser();

        commentService.create(comment, user);

        Mockito.verify(commentRepository, Mockito.times(1))
                .create(comment);
    }

    @Test
    public void update_Should_UpdateComment_When_UserIsCreator() {
        Comment comment = createMockComment();
        User user = createMockUser();

        comment.setUserId(user);

        Mockito
                .doNothing()
                .when(authorizationHelper)
                .checkOwnership(comment.getId(), user, commentRepository);

        commentService.update(comment, user);

        Mockito.verify(commentRepository, Mockito.times(1))
                .update(comment);
    }

    @Test
    public void update_Should_Throw_When_UserIsNotCreator(){
        Comment comment = createMockComment();
        User user = createMockUser();
        User notCreator = createMockUser();
        notCreator.setId(2);
        notCreator.setUsername("notcreator");
        comment.setUserId(user);

        Mockito
                .doThrow(AuthorizationException.class)
                .when(authorizationHelper)
                .checkOwnership(comment.getId(), notCreator, commentRepository);

        Assertions.assertThrows(AuthorizationException.class,
                () -> commentService.update(comment, notCreator));
    }

    @Test
    public void delete_Should_DeleteComment_When_UserIsCreatorOrAdminOrModerator(){
        Comment comment = createMockComment();
        User user = createMockUser();

        comment.setUserId(user);

        Mockito
                .doNothing()
                .when(authorizationHelper)
                .checkPermissionsAndOwnership(comment.getId(), user, commentRepository, "ADMIN", "MODERATOR");

        commentService.delete(comment.getId(), user);

        Mockito.verify(commentRepository, Mockito.times(1))
                .delete(comment.getId());
    }

    @Test
    public void delete_Should_Throw_When_UserIsNotCreatorOrAdminOrModerator(){
        Comment comment = createMockComment();
        User user = createMockUser();
        User notCreator = createMockUser();
        notCreator.setId(2);
        notCreator.setUsername("notcreator");
        comment.setUserId(user);

        Mockito
                .doThrow(AuthorizationException.class)
                .when(authorizationHelper)
                .checkPermissionsAndOwnership(comment.getId(), notCreator, commentRepository, "ADMIN", "MODERATOR");

        Assertions.assertThrows(AuthorizationException.class,
                () -> commentService.delete(comment.getId(), notCreator));
    }
}
