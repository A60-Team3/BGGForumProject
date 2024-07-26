package com.example.bggforumproject.service;

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
    public static final String BLOCK_USER_ERROR_MESSAGE = "Only user with admin rights can block people";


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
        return userRepository.findById(id);
    }

    @Override
    public User get(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public List<User> getAll(UserFilterOptions userFilterOptions) {
        return userRepository.findAll(userFilterOptions);
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
        authorizationHelper.checkPermissions(currentUser, "ADMIN", "MODERATOR");

        User userToPromote = userRepository.findById(id);
        Role role = roleRepository.findByAuthority(RoleType.MODERATOR.name());

        if (userToPromote.getRoles().contains(role)) {
            throw new EntityDuplicateException(userToPromote.getUsername(), "role", role.getAuthority());
        }

        userToPromote.getRoles().add(role);

        userRepository.update(userToPromote);

        return userToPromote;
    }

    @Override
    public User update(User loggedUser, User user) {

        authorizationHelper.validateEmailIsUnique(user.getEmail());

        if (!loggedUser.getUsername().equals(user.getUsername())) {
            throw new IllegalUsernameModificationException();
        }

        loggedUser.setFirstName(user.getFirstName());
        loggedUser.setLastName(user.getLastName());
        loggedUser.setPassword(passwordEncoder.encode(user.getPassword()));
        loggedUser.setEmail(user.getEmail());
        userRepository.update(loggedUser);

        return userRepository.findById(loggedUser.getId());
    }

    @Override
    public void blockUser(long id, User currentUser, boolean isBlocked) {
        authorizationHelper.checkPermissions(currentUser, "ADMIN", "MODERATOR");

        User user = userRepository.findById(id);
        user.setBlocked(isBlocked);
        userRepository.update(user);
    }

    @Override
    public void delete(long id, User user) {

        authorizationHelper.checkPermissions(user, "ADMIN", "MODERATOR");

        userRepository.delete(id);
    }

    @Override
    public void softDelete(long id, User currentUser) {
        authorizationHelper.checkPermissions(currentUser, "ADMIN");

        User userToArchive = userRepository.findById(id);
        userToArchive.setDeleted(true);

        userRepository.update(userToArchive);
    }
}
