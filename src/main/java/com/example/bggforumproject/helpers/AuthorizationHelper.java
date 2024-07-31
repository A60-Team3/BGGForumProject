package com.example.bggforumproject.helpers;

import com.example.bggforumproject.exceptions.AuthorizationException;
import com.example.bggforumproject.exceptions.EntityDuplicateException;
import com.example.bggforumproject.exceptions.EntityNotFoundException;
import com.example.bggforumproject.models.contracts.Ownable;
import com.example.bggforumproject.models.Role;
import com.example.bggforumproject.models.User;
import com.example.bggforumproject.repositories.contracts.UserRepository;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class AuthorizationHelper {

    public static final String ACCESS_ERROR_MESSAGE = "Only %s has access to this feature";
    public static final String OWNER_ERROR_MESSAGE = "You are not the owner of this %s";

    private final UserRepository userRepository;

    public AuthorizationHelper(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void validateEmailIsUnique(long id, String email) {
        try {
            User user = userRepository.getByEmail(email);
            if (user.getId() != id) {
                throw new EntityDuplicateException("User", "email", email);
            }
        } catch (EntityNotFoundException ignored) {
        }
    }

    public void checkPermissions(User user, String... roles) {
        Set<Role> roleSet = Arrays.stream(roles).map(Role::new).collect(Collectors.toSet());

        boolean hasPermissions = user.getRoles().stream().anyMatch(roleSet::contains);

        if (!hasPermissions) {
            throw new AuthorizationException(
                    String.format(
                            ACCESS_ERROR_MESSAGE,
                            String.join(", ", roleSet.stream().map(Role::getAuthority).toList()))
            );
        }
    }

    public <T extends Ownable> void checkOwnership(T entity, User user) {
        if (entity.getUserId().getId() != user.getId()) {
            throw new AuthorizationException(String.format(OWNER_ERROR_MESSAGE, entity.getClass().getSimpleName()));
        }
    }

    public <T extends Ownable> void checkPermissionsAndOwnership(T entity, User user, String... roles) {
        try {
            checkPermissions(user, roles);
        } catch (AuthorizationException e) {
            checkOwnership(entity, user);
        }
    }

}
