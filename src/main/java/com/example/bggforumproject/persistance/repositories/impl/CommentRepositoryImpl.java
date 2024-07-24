package com.example.bggforumproject.persistance.repositories.impl;

import com.example.bggforumproject.persistance.models.Comment;
import com.example.bggforumproject.persistance.repositories.CommentRepository;
import com.example.bggforumproject.presentation.exceptions.EntityNotFoundException;
import com.example.bggforumproject.presentation.exceptions.InvalidFilterArgumentException;
import com.example.bggforumproject.presentation.helpers.CommentFilterOptions;
import com.example.bggforumproject.presentation.helpers.PostFilterOptions;
import com.example.bggforumproject.presentation.helpers.UserFilterOptions;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class CommentRepositoryImpl implements CommentRepository {

    private static final List<String> VALID_CONDITIONS = List.of("<", "<=", ">", ">=", "=", "<>");
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final SessionFactory sessionFactory;

    @Autowired
    public CommentRepositoryImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public List<Comment> get(CommentFilterOptions commentFilterOptions) {
        try (Session session = sessionFactory.openSession()) {
            StringBuilder queryString = new StringBuilder("from Comment c");

            List<String> filters = new ArrayList<>();
            Map<String, Object> params = new HashMap<>();

            if (commentFilterOptions.getCommentedTo().isPresent()) {
                queryString.append(" join c.postId p");
            }

            if (commentFilterOptions.getCreatedBy().isPresent()) {
                queryString.append(" join c.userId u");
            }

            commentFilterOptions.getContent().ifPresent(value -> {
                filters.add("c.content like :content");
                params.put("content", String.format("%%%s%%", value));
            });

            commentFilterOptions.getCreated().ifPresent(value -> {
                String condition = value.split(",")[0];
                String date = value.split(",")[1];

                if (VALID_CONDITIONS.contains(condition)) {
                    filters.add(String.format("c.createdAt %s (:created)", condition));
                    params.put("created", LocalDateTime.parse(date, FORMATTER));
                } else {
                    throw new InvalidFilterArgumentException("Filter condition not valid");
                }
            });

            commentFilterOptions.getUpdated().ifPresent(value -> {
                String condition = value.split(",")[0];
                String date = value.split(",")[1];

                if (VALID_CONDITIONS.contains(condition)) {
                    filters.add(String.format("c.updatedAt %s (:updated)", condition));
                    params.put("updated", LocalDateTime.parse(date, FORMATTER));
                } else {
                    throw new InvalidFilterArgumentException("Filter condition not valid");
                }
            });

            commentFilterOptions.getCreatedBy().ifPresent(value -> {
                filters.add("u.id = :userId");
                params.put("userId", value);
            });

            commentFilterOptions.getCommentedTo().ifPresent(value -> {
                filters.add("p.id = :postId");
                params.put("postId", value);
            });


            if (!filters.isEmpty()) {
                queryString
                        .append(" where ")
                        .append(String.join(" and ", filters));
            }


            queryString.append(generateOrderBy(commentFilterOptions));

            Query<Comment> query = session.createQuery(queryString.toString(), Comment.class);
            query.setProperties(params);
            return query.list();
        }
    }

    @Override
    public Comment get(int id) {
        try (Session session = sessionFactory.openSession()) {
            Comment comment = session.get(Comment.class, id);


            if (comment == null) {
                throw new EntityNotFoundException("Comment", id);
            }

            return comment;
        }
    }

    @Override
    public void create(Comment comment) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.persist(comment);
            session.getTransaction().commit();
        }
    }

    @Override
    public void update(Comment comment) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.merge(comment);
            session.getTransaction().commit();
        }
    }

    @Override
    public void delete(int id) {
        Comment commentToDelete = get(id);
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.remove(commentToDelete);
            session.getTransaction().commit();
        }
    }

    private String generateOrderBy(CommentFilterOptions commentFilterOptions) {
        if (commentFilterOptions.getSortBy().isEmpty()) {
            return String.format(" order by u.id %s", determineSortOrder(commentFilterOptions));
        }

        String orderBy = switch (commentFilterOptions.getSortBy().get()) {
            case "content" -> "c.content";
            case "user" -> "c.userId";
            case "created" -> "c.createdAt";
            case "yearCreated" -> "year(c.createdAt)";
            case "monthCreated" -> "month(c.createdAt)";
            case "dayCreated" -> "day(c.createdAt)";
            case "updated" -> "c.updatedAt";
            case "yearUpdated" -> "year(c.updatedAt)";
            case "monthUpdated" -> "month(c.updatedAt)";
            case "dayUpdated" -> "day(c.updatedAt)";
            case "commentedTo" -> "c.postId";
            default -> "c.id";
        };

        orderBy = String.format(" order by %s %s", orderBy, determineSortOrder(commentFilterOptions));

        return orderBy;
    }

    private String determineSortOrder(CommentFilterOptions commentFilterOptions) {
        if (commentFilterOptions.getSortOrder().isPresent() &&
                commentFilterOptions.getSortOrder().get().equalsIgnoreCase("desc")) {
            return "desc";
        }
        return "";
    }
}
