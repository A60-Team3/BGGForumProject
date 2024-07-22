package com.example.bggforumproject.persistance.repositories.impl;

import com.example.bggforumproject.persistance.models.Post;
import com.example.bggforumproject.persistance.models.User;
import com.example.bggforumproject.persistance.repositories.PostRepository;
import com.example.bggforumproject.presentation.exceptions.EntityNotFoundException;
import com.example.bggforumproject.presentation.helpers.PostFilterOptions;
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
public class PostRepositoryImpl implements PostRepository {

    private final SessionFactory sessionFactory;

    @Autowired
    public PostRepositoryImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public List<Post> get(PostFilterOptions postFilterOptions) {
        try (Session session = sessionFactory.openSession()) {

            StringBuilder queryString = new StringBuilder("from Post p");
            if (postFilterOptions.getTagId().isPresent()) {
                queryString.append(" join p.tags t");
            }

            List<String> filters = new ArrayList<>();
            Map<String, Object> params = new HashMap<>();

            postFilterOptions.getTitle().ifPresent(value -> {
                filters.add("p.title like :title");
                params.put("title", String.format("%%%s%%", value));
            });

            postFilterOptions.getTagId().ifPresent(value -> {
                filters.add("t.id = :tagId");
                params.put("tagId", value);
            });

            if (!filters.isEmpty()) {
                queryString.append(" where ")
                        .append(String.join(" and ", filters));
            }

            queryString.append(generateOrderBy(postFilterOptions));

            Query<Post> query = session.createQuery(queryString.toString(), Post.class);
            query.setProperties(params);
            return query.list();
        }
    }

    @Override
    public Post get(int id) {
        try (Session session = sessionFactory.openSession()) {
            Post post = session.get(Post.class, id);
            if (post == null) {
                throw new EntityNotFoundException("Post", id);
            }
            return post;
        }
    }

    @Override
    public Post get(String title) {
        try (Session session = sessionFactory.openSession()) {
            Query<Post> query = session.createQuery("from Post where title = :title", Post.class);
            query.setParameter("title", title);

            List<Post> result = query.list();
            if (result.size() == 0) {
                throw new EntityNotFoundException("Post", "title", title);
            }

            return result.get(0);
        }
    }

    @Override
    public void create(Post post) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.persist(post);
            session.getTransaction().commit();
        }
    }

    @Override
    public void update(Post post) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.merge(post);
            session.getTransaction().commit();
        }
    }

    @Override
    public void delete(int id) {
        Post postToDelete = get(id);
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.remove(postToDelete);
            session.getTransaction().commit();
        }
    }

    private String generateOrderBy(PostFilterOptions postFilterOptions) {
        if (postFilterOptions.getSortBy().isEmpty()) {
            return "";
        }

        String orderBy = "";
        switch (postFilterOptions.getSortBy().get()) {
            case "title":
                orderBy = "p.title";
                break;
            case "date":
                orderBy = "p.created_at";
                break;
        }

        orderBy = String.format(" order by %s", orderBy);

        if (postFilterOptions.getSortOrder().isPresent() && postFilterOptions.getSortOrder().get().equalsIgnoreCase("desc")) {
            orderBy = String.format("%s desc", orderBy);
        }

        return orderBy;
    }
}
