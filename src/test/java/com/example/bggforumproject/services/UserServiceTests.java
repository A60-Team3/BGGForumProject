package com.example.bggforumproject.services;

import com.example.bggforumproject.exceptions.AuthorizationException;
import com.example.bggforumproject.exceptions.EntityDuplicateException;
import com.example.bggforumproject.exceptions.EntityNotFoundException;
import com.example.bggforumproject.exceptions.IllegalUsernameModificationException;
import com.example.bggforumproject.helpers.AuthorizationHelper;
import com.example.bggforumproject.helpers.filters.CommentFilterOptions;
import com.example.bggforumproject.helpers.filters.PostFilterOptions;
import com.example.bggforumproject.helpers.filters.UserFilterOptions;
import com.example.bggforumproject.models.Role;
import com.example.bggforumproject.models.User;
import com.example.bggforumproject.models.enums.RoleType;
import com.example.bggforumproject.repositories.contracts.CommentRepository;
import com.example.bggforumproject.repositories.contracts.PostRepository;
import com.example.bggforumproject.repositories.contracts.RoleRepository;
import com.example.bggforumproject.repositories.contracts.UserRepository;
import com.example.bggforumproject.service.UserServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static com.example.bggforumproject.helpers.CreationHelper.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTests {
    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;
    @Mock
    private PostRepository postRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private AuthorizationHelper authorizationHelper;

    @Test
    public void get_Should_CallRepository_WhenIDIsProvided() {
        userService.get(1);

        Mockito.verify(userRepository, Mockito.times(1))
                .getById(1);
    }

    @Test
    public void get_Should_CallRepository_WhenUsernameIsProvided() {
        userService.get("username");

        Mockito.verify(userRepository, Mockito.times(1))
                .getByUsername("username");
    }

    @Test
    public void get_Should_ReturnUser_When_IdProvided() {
        User user = createMockUser();

        Mockito.when(userRepository.getById(user.getId()))
                .thenReturn(user);

        User idResult = userService.get(user.getId());

        Assertions.assertSame(user, idResult);
    }

    @Test
    public void get_Should_ReturnUser_When_IdOrUsernameSupplied() {
        User user = createMockUser();

        Mockito.when(userRepository.getByUsername(user.getUsername()))
                .thenReturn(user);

        User usernameResult = userService.get(user.getUsername());

        Assertions.assertSame(user, usernameResult);
    }

    @Test
    public void getAll_Should_CallRepository() {
        UserFilterOptions mockFilterOptions = createMockUserFilterOptions();
        int pageIndex = 0;
        int pageSize = 5;

        Mockito.when(userRepository.getAll(mockFilterOptions, pageIndex, pageSize))
                .thenReturn(null);

        userService.getAll(mockFilterOptions, pageIndex, pageSize);

        Mockito.verify(userRepository, Mockito.times(1))
                .getAll(mockFilterOptions, pageIndex, pageSize);
    }

    @Test
    public void getSpecificUserPosts_Should_CallRepository() {
        PostFilterOptions mockPostFilterOptions = createMockPostFilterOptions();
        int pageIndex = 0;
        int pageSize = 10;

        Mockito.when(postRepository.get(mockPostFilterOptions, pageIndex, pageSize))
                .thenReturn(null);

        Mockito.when(userRepository.getById(Mockito.anyLong()))
                .thenReturn(Mockito.any(User.class));

        userService.getSpecificUserPosts(1, pageIndex, pageSize);

        Mockito.verify(postRepository, Mockito.times(1))
                .get(mockPostFilterOptions, pageIndex, pageSize);
        Mockito.verify(userRepository, Mockito.times(1))
                .getById(1);
    }

    @Test
    public void getSpecificUserComments_Should_CallRepository() {
        CommentFilterOptions mockCommentFilterOptions = createMockCommentFilterOptions();

        int pageIndex = 0;
        int pageSize = 10;

        Mockito.when(commentRepository.get(mockCommentFilterOptions, pageIndex, pageSize))
                .thenReturn(null);

        Mockito.when(userRepository.getById(Mockito.anyLong()))
                .thenReturn(Mockito.any(User.class));

        userService.getSpecificUserComments(1, pageIndex, pageSize);

        Mockito.verify(commentRepository, Mockito.times(1))
                .get(mockCommentFilterOptions, pageIndex, pageSize);
        Mockito.verify(userRepository, Mockito.times(1))
                .getById(1);
    }

    @Test
    public void promote_Should_Throw_When_UserIsNotAdmin() {
        User user = createMockUser();

        Mockito
                .doThrow(AuthorizationException.class)
                .when(authorizationHelper)
                .checkPermissions(user, RoleType.ADMIN);

        Assertions.assertThrows(
                AuthorizationException.class,
                () -> userService.promote(2, user)
        );
    }

    @Test
    public void promote_Should_Throw_When_UserToPromoteIdIsNotValid() {
        User user = createMockUser();

        Mockito
                .doNothing()
                .when(authorizationHelper)
                .checkPermissions(user, RoleType.ADMIN);

        Mockito
                .when(userRepository.getById(Mockito.anyLong()))
                .thenThrow(EntityNotFoundException.class);

        Assertions.assertThrows(
                EntityNotFoundException.class,
                () -> userService.promote(2, user)
        );
    }

    @Test
    public void promote_Should_Throw_When_UserToPromoteIsSameRole() {
        User user = createMockModerator();

        Mockito
                .doNothing()
                .when(authorizationHelper)
                .checkPermissions(user, RoleType.ADMIN);

        Mockito
                .when(userRepository.getById(Mockito.anyLong()))
                .thenReturn(user);

        Mockito
                .when(roleRepository.getByAuthority(Mockito.any()))
                .thenReturn(new Role(RoleType.MODERATOR));

        Assertions.assertThrows(
                EntityDuplicateException.class,
                () -> userService.promote(1, user)
        );
    }

    @Test
    public void promote_Should_ReturnPromotedUser_When_InputIsValid() {
        User user = createMockUser();

        Mockito
                .doNothing()
                .when(authorizationHelper)
                .checkPermissions(user, RoleType.ADMIN);

        Mockito
                .when(userRepository.getById(Mockito.anyLong()))
                .thenReturn(user);

        Mockito
                .when(roleRepository.getByAuthority(Mockito.any()))
                .thenReturn(new Role(RoleType.MODERATOR));

        User promoted = userService.promote(1, user);

        user.getRoles().add(new Role(RoleType.MODERATOR));

        Mockito.verify(userRepository, Mockito.times(1)).update(user);

        Assertions.assertSame(user, promoted);
    }

    @Test
    public void update_Should_Throw_When_UpdatedUserIsNotTheLoggedUser() {
        Assertions
                .assertThrows(
                        AuthorizationException.class,
                        () -> userService.update(2, createMockUser(), createMockUser()));
    }

    @Test
    public void update_Should_Throw_When_EmailIsNotUnique() {
        Mockito
                .doThrow(EntityDuplicateException.class)
                .when(authorizationHelper)
                .validateEmailIsUnique(Mockito.anyLong(), Mockito.anyString());

        Assertions.assertThrows(
                EntityDuplicateException.class,
                () -> userService
                        .update(createMockUser().getId(), createMockUser(), createMockUser())
        );
    }

    @Test
    public void update_Should_Throw_When_UsernameIsChanged() {
        Mockito
                .doNothing()
                .when(authorizationHelper)
                .validateEmailIsUnique(Mockito.anyLong(), Mockito.anyString());

        User user = createMockUser();
        User updated = createMockUser();
        updated.setUsername("FightClub");

        Assertions.assertThrows(
                IllegalUsernameModificationException.class,
                () -> userService
                        .update(user.getId(), user, updated)
        );
    }

    @Test
    public void update_Should_ReturnUpdatedUser_When_InputIsValid() {
        User user = createMockUser();
        User updated = createMockUser();
        updated.setFirstName("changed");
        updated.setLastName("changed");

        Mockito
                .doNothing()
                .when(authorizationHelper)
                .validateEmailIsUnique(Mockito.anyLong(), Mockito.anyString());

        Mockito
                .when(userRepository.getById(Mockito.anyLong()))
                .thenReturn(updated);

        Mockito
                .when(passwordEncoder.encode(Mockito.anyString()))
                .thenReturn(user.getPassword());

        User afterUpdate = userService.update(user.getId(), user, updated);

        Mockito.verify(userRepository, Mockito.times(1)).update(user);
        Mockito.verify(userRepository, Mockito.times(1)).getById(user.getId());

        Assertions.assertAll(
                () -> Assertions.assertEquals(user.getFirstName(), afterUpdate.getFirstName()),
                () -> Assertions.assertEquals(user.getLastName(), afterUpdate.getLastName())
        );
    }

    @Test
    public void blockUser_Should_Throw_When_UserIsNotAdminOrMod() {
        User user = createMockUser();
        Mockito
                .doThrow(AuthorizationException.class)
                .when(authorizationHelper)
                .checkPermissions(Mockito.any(), Mockito.any(), Mockito.any());

        Assertions.assertThrows(
                AuthorizationException.class,
                () -> userService.blockUser(1, user)
        );
    }

    @Test
    public void blockUser_Should_Throw_When_IDisNotValid() {
        User user = createMockUser();
        Mockito
                .doNothing()
                .when(authorizationHelper)
                .checkPermissions(Mockito.any(), Mockito.any(), Mockito.any());

        Mockito
                .when(userRepository.getById(Mockito.anyLong()))
                .thenThrow(EntityNotFoundException.class);

        Assertions.assertThrows(
                EntityNotFoundException.class,
                () -> userService.blockUser(1, user)
        );
    }

    @Test
    public void blockUser_Should_CallRepository_When_InputIsValid() {
        User user = createMockUser();
        Mockito
                .doNothing()
                .when(authorizationHelper)
                .checkPermissions(Mockito.any(), Mockito.any(), Mockito.any());

        Mockito
                .when(userRepository.getById(Mockito.anyLong()))
                .thenReturn(user);

        userService.blockUser(user.getId(), user);

        Mockito.verify(userRepository, Mockito.times(1)).update(user);
        Mockito.verify(userRepository, Mockito.times(1)).getById(user.getId());

        Assertions.assertTrue(user.isBlocked());
    }

    @Test
    public void softDelete_Should_Throw_When_UserIsNotAdmin() {
        User user = createMockUser();
        Mockito
                .doThrow(AuthorizationException.class)
                .when(authorizationHelper)
                .checkPermissions(Mockito.any(), Mockito.any());

        Assertions.assertThrows(
                AuthorizationException.class,
                () -> userService.softDelete(1, user)
        );
    }

    @Test
    public void softDelete_Should_Throw_When_IDisNotValid() {
        User user = createMockUser();
        Mockito
                .doNothing()
                .when(authorizationHelper)
                .checkPermissions(Mockito.any(), Mockito.any());

        Mockito
                .when(userRepository.getById(Mockito.anyLong()))
                .thenThrow(EntityNotFoundException.class);

        Assertions.assertThrows(
                EntityNotFoundException.class,
                () -> userService.softDelete(1, user)
        );
    }

    @Test
    public void softDelete_Should_CallRepository_When_InputIsValid() {
        User admin = createMockAdmin();
        User user = createMockUser();

        Mockito
                .doNothing()
                .when(authorizationHelper)
                .checkPermissions(Mockito.any(), Mockito.any());

        Mockito
                .when(userRepository.getById(Mockito.anyLong()))
                .thenReturn(user);

        userService.softDelete(user.getId(), admin);

        Mockito.verify(userRepository, Mockito.times(1)).update(user);
        Mockito.verify(userRepository, Mockito.times(1)).getById(user.getId());

        Assertions.assertTrue(user.isDeleted());
    }

    @Test
    public void delete_Should_Throw_When_UserIsNotAdmin() {
        User user = createMockUser();
        Mockito
                .doThrow(AuthorizationException.class)
                .when(authorizationHelper)
                .checkPermissions(Mockito.any(), Mockito.any());

        Assertions.assertThrows(
                AuthorizationException.class,
                () -> userService.softDelete(1, user)
        );
    }

    @Test
    public void delete_Should_Throw_When_IDisNotValid() {
        User user = createMockUser();
        Mockito
                .doNothing()
                .when(authorizationHelper)
                .checkPermissions(Mockito.any(), Mockito.any());

        Mockito
                .when(userRepository.getById(Mockito.anyLong()))
                .thenThrow(EntityNotFoundException.class);

        Assertions.assertThrows(
                EntityNotFoundException.class,
                () -> userService.softDelete(1, user)
        );
    }

    @Test
    public void delete_Should_CallRepository_When_InputIsValid() {
        User admin = createMockAdmin();
        User user = createMockUser();

        Mockito
                .doNothing()
                .when(authorizationHelper)
                .checkPermissions(Mockito.any(), Mockito.any());

        Mockito
                .when(userRepository.getById(Mockito.anyLong()))
                .thenReturn(user);

        userService.delete(user.getId(), admin);

        Mockito.verify(userRepository, Mockito.times(1)).delete(user);
        Mockito.verify(userRepository, Mockito.times(1)).getById(user.getId());

    }
}
