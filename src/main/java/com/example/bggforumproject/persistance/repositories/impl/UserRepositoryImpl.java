package com.example.bggforumproject.persistance.repositories.impl;

import com.example.bggforumproject.persistance.models.User;
import com.example.bggforumproject.persistance.repositories.UserRepository;
import com.example.bggforumproject.presentation.exceptions.EntityNotFoundException;
import com.example.bggforumproject.presentation.exceptions.InvalidFilterArgumentException;
import com.example.bggforumproject.presentation.helpers.UserFilterOptions;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Repository
public class UserRepositoryImpl implements UserRepository {

    private static final List<String> VALID_CONDITIONS = List.of("<", "<=", ">", ">=", "=", "<>");
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final SessionFactory sessionFactory;

    public UserRepositoryImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public User findByUsername(String username) {
        try (Session session = sessionFactory.openSession()) {
            Query<User> query = session.createQuery("from User where username = :username", User.class);
            query.setParameter("username", username);

            if (query.list().isEmpty()) {
                throw new EntityNotFoundException("User", "username", username);
            }
            return query.list().get(0);
        }

    }

    @Override
    public User findByEmail(String email) {
        try (Session session = sessionFactory.openSession()) {
            Query<User> query = session.createQuery("from User where email = :email", User.class);
            query.setParameter("email", email);

            if (query.list().isEmpty()) {
                throw new EntityNotFoundException("User", "email", email);
            }
            return query.list().get(0);
        }
    }

    @Override
    public User findById(long id) {
        try (Session session = sessionFactory.openSession()) {
            User user = session.get(User.class, id);
            if (user == null) {
                throw new EntityNotFoundException("User", id);
            }
            return user;
        }
    }

    @Override
    public List<User> findAll() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("from User ", User.class).list();
        }
    }

    @Override
    public List<User> findAll(UserFilterOptions userFilterOptions) {
        try (Session session = sessionFactory.openSession()) {
            StringBuilder queryString = new StringBuilder("from User u");

            if (userFilterOptions.getAuthority().isPresent() ||
                    (userFilterOptions.getSortBy().isPresent()
                            && userFilterOptions.getSortBy().get().equals("authority"))) {
                queryString.append(" join u.authorities r");
            }

            List<String> filters = new ArrayList<>();
            Map<String, Object> params = new HashMap<>();

            userFilterOptions.getFirstName().ifPresent(value -> {
                filters.add("u.firstName like :firstName");
                params.put("firstName", String.format("%%%s%%", value.trim()));
            });

            userFilterOptions.getLastName().ifPresent(value -> {
                filters.add("u.lastName like :lastName");
                params.put("lastName", String.format("%%%s%%", value.trim()));
            });

            userFilterOptions.getEmail().ifPresent(value -> {
                filters.add("u.email like :email");
                params.put("email", String.format("%%%s%%", value.trim()));
            });

            userFilterOptions.getUsername().ifPresent(value -> {
                filters.add("u.username like :username");
                params.put("username", String.format("%%%s%%", value.trim()));
            });

            userFilterOptions.getRegistered().ifPresent(value -> {
                String condition = value.split(",")[0];
                String date = value.split(",")[1];

                if (VALID_CONDITIONS.contains(condition)) {
                    filters.add(String.format("u.registeredAt %s (:registered)", condition));
                    params.put("registered", LocalDateTime.parse(date.trim(), FORMATTER));
                } else {
                    throw new InvalidFilterArgumentException("Filter condition not valid");
                }
            });

            userFilterOptions.getUpdated().ifPresent(value -> {
                String condition = value.split(",")[0];
                String date = value.split(",")[1];

                if (VALID_CONDITIONS.contains(condition)) {
                    filters.add(String.format("u.updatedAt %s (:updated)", condition));
                    params.put("updated", LocalDateTime.parse(date.trim(), FORMATTER));
                } else {
                    throw new InvalidFilterArgumentException("Filter condition not valid");
                }
            });

            userFilterOptions.getIsBlocked().ifPresent(value -> {
                filters.add("u.isBlocked = :isBlocked");
                params.put("isBlocked", value);
            });

            userFilterOptions.getIsDeleted().ifPresent(value -> {
                filters.add("u.isDeleted = :isDeleted");
                params.put("isDeleted", value);
            });

            userFilterOptions.getAuthority().ifPresent(value -> {
                List<String> roles = Arrays.stream(value.split(",")).toList();

                filters.add("LOWER(r.authority) IN (:roleType");
                params.put("roleType", roles);
            });

            if (!filters.isEmpty()) {
                queryString.append(" where ")
                        .append(String.join(" and ", filters));
            }

            queryString.append(generateOrderBy(userFilterOptions));

            Query<User> query = session.createQuery(queryString.toString(), User.class);
            query.setProperties(params);
            return query.list();
        }
    }


    @Override
    public User create(User user) {

        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.persist(user);
            session.getTransaction().commit();
        }

        return findByUsername(user.getUsername());
    }

    @Override
    public void update(User user) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.merge(user);
            session.getTransaction().commit();
        }
    }

    @Override
    public void delete(long id) {
        User userToDelete = findById(id);
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.remove(userToDelete);
            session.getTransaction().commit();
        }
    }

    private String generateOrderBy(UserFilterOptions userFilterOptions) {
        if (userFilterOptions.getSortBy().isEmpty()) {
            return String.format(" order by u.id %s", determineSortOrder(userFilterOptions));
        }

        String orderBy = switch (userFilterOptions.getSortBy().get()) {
            case "firstName" -> "u.firstName";
            case "lastName" -> "u.lastName";
            case "email" -> "u.email";
            case "username" -> "u.username";
            case "registered" -> "u.registeredAt";
            case "yearRegistered" -> "year(u.registeredAt)";
            case "monthRegistered" -> "month(u.registeredAt)";
            case "dayRegistered" -> "day(u.registeredAt)";
            case "updated" -> "u.updatedAt";
            case "yearUpdated" -> "year(u.updatedAt)";
            case "monthUpdated" -> "month(u.updatedAt)";
            case "dayUpdated" -> "day(u.updatedAt)";
            case "isBlocked" -> "u.isBlocked";
            case "isDeleted" -> "u.isDeleted";
            case "authority" -> "r.authority";
            default -> "u.id";
        };

        orderBy = String.format(" order by %s %s", orderBy, determineSortOrder(userFilterOptions));

        return orderBy;
    }

    private String determineSortOrder(UserFilterOptions userFilterOptions) {
        if (userFilterOptions.getSortOrder().isPresent() &&
                userFilterOptions.getSortOrder().get().equalsIgnoreCase("desc")) {
            return "desc";
        }
        return "";
    }
}

