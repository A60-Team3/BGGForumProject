package com.example.bggforumproject.service;

import com.example.bggforumproject.exceptions.AuthorizationException;
import com.example.bggforumproject.exceptions.EntityDuplicateException;
import com.example.bggforumproject.exceptions.IllegalUsernameModificationException;
import com.example.bggforumproject.helpers.AuthorizationHelper;
import com.example.bggforumproject.helpers.CommentFilterOptions;
import com.example.bggforumproject.helpers.PostFilterOptions;
import com.example.bggforumproject.helpers.UserFilterOptions;
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
    public List<Post> getSpecificUserPosts(PostFilterOptions postFilterOptions) {
        return postRepository.get(postFilterOptions);
    }

    @Override
    public List<Comment> getSpecificUserComments(CommentFilterOptions commentFilterOptions) {
        return commentRepository.get(commentFilterOptions);
    }

    @Override
    public User promote(long id, User currentUser) {
        authorizationHelper.checkPermissions(currentUser, "ADMIN");

        User userToPromote = userRepository.getById(id);
        Role role = roleRepository.getByAuthority(RoleType.MODERATOR.name());

        if (userToPromote.getRoles().contains(role)) {
            throw new EntityDuplicateException(userToPromote.getUsername(), "role", role.getAuthority());
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
    public void blockUser(long id, User currentUser, boolean isBlocked) {
        authorizationHelper.checkPermissions(currentUser, "ADMIN", "MODERATOR");

        User user = userRepository.getById(id);
        user.setBlocked(isBlocked);
        userRepository.update(user);
    }

    @Override
    public void softDelete(long id, User currentUser) {
        authorizationHelper.checkPermissions(currentUser, "ADMIN");

        User userToArchive = userRepository.getById(id);
        userToArchive.setDeleted(true);

        userRepository.update(userToArchive);
    }

    @Override
    public void delete(long id, User user) {
        authorizationHelper.checkPermissions(user, "ADMIN");

        User userToDelete = userRepository.getById(id);

        userRepository.delete(userToDelete);
    }
}
