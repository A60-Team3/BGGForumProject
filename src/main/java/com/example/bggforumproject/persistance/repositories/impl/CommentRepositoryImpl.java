package com.example.bggforumproject.persistance.repositories.impl;

import com.example.bggforumproject.persistance.models.Comment;
import com.example.bggforumproject.persistance.repositories.CommentRepository;
import com.example.bggforumproject.presentation.exceptions.EntityNotFoundException;
import com.example.bggforumproject.presentation.helpers.CommentFilterOptions;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class CommentRepositoryImpl implements CommentRepository {

    private final SessionFactory sessionFactory;

    @Autowired
    public CommentRepositoryImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public List<Comment> get(CommentFilterOptions commentFilterOptions) {
        try(Session session = sessionFactory.openSession()){
            StringBuilder queryString = new StringBuilder("from Comment");

            List<String> filters = new ArrayList<>();
            Map<String, Object> params = new HashMap<>();

            commentFilterOptions.getCreatedBefore().ifPresent(value -> {
                filters.add("createdAt < :createdBefore");
                params.put("createdBefore", value);
            });

            commentFilterOptions.getCreatedAfter().ifPresent(value -> {
                filters.add("createdAt > :createdAfter");
                params.put("createdAfter", value);
            });

            commentFilterOptions.getCreatedBy().ifPresent(value -> {
                filters.add("userId = :userId");
                params.put("userId", value);
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
        try(Session session = sessionFactory.openSession()){
            Comment comment = session.get(Comment.class, id);


            if (comment == null){
                throw new EntityNotFoundException("Comment", id);
            }

            return comment;
        }
    }

    @Override
    public void create(Comment comment) {
        try(Session session = sessionFactory.openSession()){
            session.beginTransaction();
            session.persist(comment);
            session.getTransaction().commit();
        }
    }

    @Override
    public void update(Comment comment) {
        try(Session session = sessionFactory.openSession()){
            session.beginTransaction();
            session.merge(comment);
            session.getTransaction().commit();
        }
    }

    @Override
    public void delete(int id) {
        Comment commentToDelete = get(id);
        try(Session session = sessionFactory.openSession()){
            session.beginTransaction();
            session.remove(commentToDelete);
            session.getTransaction().commit();
        }
    }

    private String generateOrderBy(CommentFilterOptions commentFilterOptions) {
        if (commentFilterOptions.getSortBy().isEmpty()) {
            return "";
        }

        String orderBy = "createdAt";
        orderBy = String.format(" order by %s", orderBy);

        if (commentFilterOptions.getSortOrder().isPresent() && commentFilterOptions.getSortOrder().get().equalsIgnoreCase("desc")) {
            orderBy = String.format("%s desc", orderBy);
        }

        return orderBy;
    }
}
