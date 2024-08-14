package com.example.bggforumproject.service;

import com.example.bggforumproject.exceptions.*;
import com.example.bggforumproject.helpers.AuthorizationHelper;
import com.example.bggforumproject.helpers.filters.CommentFilterOptions;
import com.example.bggforumproject.helpers.filters.PostFilterOptions;
import com.example.bggforumproject.helpers.filters.UserFilterOptions;
import com.example.bggforumproject.models.Comment;
import com.example.bggforumproject.models.Post;
import com.example.bggforumproject.models.Role;
import com.example.bggforumproject.models.User;
import com.example.bggforumproject.models.enums.RoleType;
import com.example.bggforumproject.repositories.contracts.CommentRepository;
import com.example.bggforumproject.repositories.contracts.PostRepository;
import com.example.bggforumproject.repositories.contracts.RoleRepository;
import com.example.bggforumproject.repositories.contracts.UserRepository;
import com.example.bggforumproject.service.contacts.UserService;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class UserServiceImpl implements UserService {

    private static final String MODIFY_USER_ERROR_MESSAGE = " Only a user can modified his info!";

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthorizationHelper authorizationHelper;

    public UserServiceImpl(UserRepository userRepository, AuthorizationHelper authorizationHelper, PostRepository postRepository,
                           CommentRepository commentRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.authorizationHelper = authorizationHelper;
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User get(long id) {
        return userRepository.getById(id);
    }

    @Override
    public User get(String username) {
        return userRepository.getByUsername(username);
    }

    @Override
    public List<User> getAll(UserFilterOptions userFilterOptions) {
        return userRepository.getAll(userFilterOptions);
    }

    @Override
    public Page<Post> getSpecificUserPosts(long id, PostFilterOptions postFilterOptions, int pageIndex, int pageSize) {
        userRepository.getById(id);

        return postRepository.get(postFilterOptions, pageIndex, pageSize);
    }

    @Override
    public Page<Comment> getSpecificUserComments(long id, CommentFilterOptions commentFilterOptions, int pageIndex, int pageSize) {
        userRepository.getById(id);

        return commentRepository.get(commentFilterOptions, pageIndex, pageSize);
    }

    @Override
    public User promote(long id, User currentUser) {
        authorizationHelper.checkPermissions(currentUser, RoleType.ADMIN);

        User userToPromote = userRepository.getById(id);

        Role role = roleRepository.getByAuthority(RoleType.MODERATOR);

        if (userToPromote.getRoles().contains(role)) {
            throw new EntityDuplicateException(
                    String.format("User with id %d is already %s", id, role.getAuthority())
            );
        }

        userToPromote.getRoles().add(role);

        userRepository.update(userToPromote);

        return userToPromote;
    }

    @Override
    public User update(long id, User loggedUser, User user) {

        if (loggedUser.getId() != id) {
            throw new AuthorizationException(MODIFY_USER_ERROR_MESSAGE);
        }

        authorizationHelper.validateEmailIsUnique(loggedUser.getId(), user.getEmail());

        if (!loggedUser.getUsername().equals(user.getUsername())) {
            throw new IllegalUsernameModificationException();
        }

        loggedUser.setFirstName(user.getFirstName());
        loggedUser.setLastName(user.getLastName());
        loggedUser.setPassword(passwordEncoder.encode(user.getPassword()));
        loggedUser.setEmail(user.getEmail());
        userRepository.update(loggedUser);

        return userRepository.getById(loggedUser.getId());
    }

    @Override
    public User blockUser(long id, User currentUser, boolean isBlocked) {
        authorizationHelper.checkPermissions(currentUser, RoleType.ADMIN, RoleType.MODERATOR);

        User user = userRepository.getById(id);

        if (user.isBlocked() == isBlocked) {
            throw new EntityDuplicateException(
                    String.format("Block status of user with id %d already set to %s", id, isBlocked)
            );
        }

        user.setBlocked(isBlocked);
        userRepository.update(user);

        return user;
    }

    @Override
    public void softDelete(long id, User currentUser) {
        authorizationHelper.checkPermissions(currentUser, RoleType.ADMIN);

        User userToArchive = userRepository.getById(id);

        if (userToArchive.isDeleted()) {
            throw new EntityDuplicateException(
                    String.format("User with id %d already archived", id)
            );
        }

        userToArchive.setDeleted(true);

        userRepository.update(userToArchive);
    }

    @Override
    public void delete(long id, User user) {
        authorizationHelper.checkPermissions(user, RoleType.ADMIN);

        User userToDelete = userRepository.getById(id);

        userRepository.delete(userToDelete);
    }
}
