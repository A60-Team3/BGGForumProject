package com.example.bggforumproject.service.impl;

import com.example.bggforumproject.persistance.models.Comment;
import com.example.bggforumproject.persistance.models.Post;
import com.example.bggforumproject.persistance.models.Role;
import com.example.bggforumproject.persistance.models.User;
import com.example.bggforumproject.persistance.models.enums.RoleType;
import com.example.bggforumproject.persistance.repositories.CommentRepository;
import com.example.bggforumproject.persistance.repositories.PostRepository;
import com.example.bggforumproject.persistance.repositories.RoleRepository;
import com.example.bggforumproject.persistance.repositories.UserRepository;
import com.example.bggforumproject.presentation.dtos.BlockDTO;
import com.example.bggforumproject.presentation.exceptions.AuthorizationException;
import com.example.bggforumproject.presentation.exceptions.EntityDuplicateException;
import com.example.bggforumproject.presentation.exceptions.IllegalUsernameModificationException;
import com.example.bggforumproject.presentation.helpers.CommentFilterOptions;
import com.example.bggforumproject.presentation.helpers.PostFilterOptions;
import com.example.bggforumproject.presentation.helpers.UserFilterOptions;
import com.example.bggforumproject.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    public static final String SUPER_USER_ERROR_MESSAGE = "Only an admin has access";
    public static final String BLOCK_USER_ERROR_MESSAGE = "Only user with admin rights can block people";


    private final UserRepository userRepository;

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, PostRepository postRepository,
                           CommentRepository commentRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
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
        checkSuperAdminPermissions(currentUser);

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
    public void blockUser(long id, User currentUser, BlockDTO dto) {
        checkAdminPermissions(currentUser);

        User user = userRepository.findById(id);
        user.setBlocked(dto.isBlocked());
        userRepository.update(user);
    }

    @Override
    public void delete(long id, User user) {

        checkSuperAdminPermissions(user);

        userRepository.delete(id);
    }

    @Override
    public void softDelete(long id, User currentUser) {
        checkSuperAdminPermissions(currentUser);

        User userToArchive = userRepository.findById(id);
        userToArchive.setDeleted(true);

        userRepository.update(userToArchive);
    }

    private void checkSuperAdminPermissions(User user) {
        boolean isAdmin = user.getRoles().stream()
                .map(Role::getAuthority)
                .anyMatch(authority -> authority.equals(RoleType.ADMIN.toString()));

        if (!isAdmin) {
            throw new AuthorizationException(SUPER_USER_ERROR_MESSAGE);
        }
    }

    private void checkAdminPermissions(User user) {
        Role admin = new Role(RoleType.ADMIN.name());
        Role moderator = new Role(RoleType.MODERATOR.name());

        boolean hasPermission = user.getRoles().contains(admin) || user.getRoles().contains(moderator);

        if (!hasPermission) {
            throw new AuthorizationException(BLOCK_USER_ERROR_MESSAGE);
        }
    }

}
