package com.example.bggforumproject.services;

import com.example.bggforumproject.exceptions.AuthorizationException;
import com.example.bggforumproject.exceptions.EntityDuplicateException;
import com.example.bggforumproject.exceptions.EntityNotFoundException;
import com.example.bggforumproject.models.Post;
import com.example.bggforumproject.models.Reaction;
import com.example.bggforumproject.models.User;
import com.example.bggforumproject.repositories.contracts.PostRepository;
import com.example.bggforumproject.repositories.contracts.ReactionRepository;
import com.example.bggforumproject.service.ReactionServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.example.bggforumproject.helpers.CreationHelper.*;

@ExtendWith(MockitoExtension.class)
public class ReactionServiceTests {

    @InjectMocks
    private ReactionServiceImpl reactionService;

    @Mock
    private ReactionRepository reactionRepository;

    @Mock
    private PostRepository postRepository;

    @Test
    public void getAll_Should_CallRepository(){
        Reaction reaction = createMockReaction();
        Post post = createMockPost();

        reaction.setPostId(post);

        Mockito.when(postRepository.get(post.getId())).thenReturn(post);
        reactionService.getAll(post.getId());

        Mockito.verify(reactionRepository, Mockito.times(1))
                .getAllForSpecificPost(post.getId());
    }

    @Test
    public void get_Should_CallRepository(){
        Reaction reaction = createMockReaction();

        Mockito.when(reactionRepository.get(1)).thenReturn(reaction);
        reactionService.get(reaction.getId());

        Mockito.verify(reactionRepository, Mockito.times(1))
                .get(reaction.getId());
    }

    @Test
    public void get_Should_ReturnReaction_When_ReactionExists(){
        Reaction reaction = createMockReaction();

        Mockito.when(reactionRepository.get(1)).thenReturn(reaction);
        Reaction result = reactionService.get(reaction.getId());

        Assertions.assertSame(reaction, result);
    }

    @Test
    public void create_Should_CreateReaction_When_UserHasNotReactedOnPost(){
        Reaction reaction = createMockReaction();
        Post post = createMockPost();
        User user = createMockUser();

        reaction.setUserId(user);

        Mockito.when(reactionRepository.getByPostAndUser(user.getId(), post.getId()))
                .thenThrow(EntityNotFoundException.class);

        reactionService.create(reaction, user, post.getId());
        Mockito.verify(reactionRepository, Mockito.times(1))
                .create(reaction);
    }

    @Test
    public void create_Should_Throw_When_UserHasAlreadyReactedOnPost(){
        Reaction reaction = createMockReaction();
        Post post = createMockPost();
        User user = createMockUser();

        reaction.setUserId(user);

        Mockito.when(reactionRepository.getByPostAndUser(user.getId(), post.getId()))
                .thenReturn(reaction);

        Assertions.assertThrows(EntityDuplicateException.class,
                () -> reactionService.create(reaction, user, post.getId()));
    }

    @Test
    public void update_Should_UpdateReaction_When_UserIsCreator(){
        Reaction reaction = createMockReaction();
        Post post = createMockPost();
        User user = createMockUser();

        reaction.setUserId(user);
        reaction.setPostId(post);

        Mockito.when(reactionRepository.get(reaction.getId())).thenReturn(reaction);
        Mockito.when(reactionRepository.getByPostAndUser(user.getId(), post.getId()))
                .thenThrow(EntityNotFoundException.class);

        reactionService.update(reaction.getId(), user, post.getId(), reaction.getReactionType());
        Mockito.verify(reactionRepository, Mockito.times(1))
                .update(reaction);
    }

    @Test
    public void update_Should_Throw_When_UserIsNotCreator(){
        Reaction reaction = createMockReaction();
        Post post = createMockPost();
        User user = createMockUser();
        User notCreator = createMockUser();
        notCreator.setId(2);
        notCreator.setUsername("notcreator");
        reaction.setUserId(user);
        reaction.setPostId(post);

        Mockito.when(reactionRepository.get(reaction.getId())).thenReturn(reaction);

        Assertions.assertThrows(AuthorizationException.class,
                () -> reactionService.update(reaction.getId(), notCreator, post.getId(), reaction.getReactionType()));
    }

    @Test
    public void update_Should_Throw_When_DuplicateExists(){
        Reaction reaction = createMockReaction();
        Reaction reactionToUpdate = createMockReaction();
        reactionToUpdate.setId(2);
        Post post = createMockPost();
        User user = createMockUser();

        reaction.setUserId(user);

        Mockito.when(reactionRepository.get(Mockito.anyLong())).thenReturn(reactionToUpdate);
        Mockito.when(reactionRepository.getByPostAndUser(user.getId(), post.getId()))
                .thenReturn(reaction);

        Assertions.assertThrows(EntityDuplicateException.class,
                () -> reactionService.update(reactionToUpdate.getId(), user, post.getId(), reaction.getReactionType()));
    }

    @Test
    public void delete_Should_DeleteReaction_When_UserIsCreator(){
        Reaction reaction = createMockReaction();
        User user = createMockUser();

        reaction.setUserId(user);

        Mockito.when(reactionRepository.get(reaction.getId())).thenReturn(reaction);
        reactionService.delete(reaction.getId(), user, reaction.getPostId().getId());
        Mockito.verify(reactionRepository, Mockito.times(1))
                .delete(reaction);
    }

    @Test
    public void delete_Should_Throw_When_UserIsNotCreator(){
        Reaction reaction = createMockReaction();
        User user = createMockUser();
        User notCreator = createMockUser();
        notCreator.setId(2);
        notCreator.setUsername("notcreator");
        reaction.setUserId(user);

        Mockito.when(reactionRepository.get(reaction.getId())).thenReturn(reaction);

        Assertions.assertThrows(AuthorizationException.class,
                () -> reactionService.delete(reaction.getId(), notCreator, reaction.getPostId().getId()));
    }
}
