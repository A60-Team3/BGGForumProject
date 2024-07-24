package com.example.bggforumproject.persistance.repositories.impl;

import com.example.bggforumproject.persistance.models.Post;
import com.example.bggforumproject.persistance.models.User;
import com.example.bggforumproject.persistance.repositories.PostRepository;
import com.example.bggforumproject.persistance.repositories.UserRepository;
import com.example.bggforumproject.presentation.exceptions.EntityNotFoundException;
import com.example.bggforumproject.presentation.exceptions.InvalidFilterArgumentException;
import com.example.bggforumproject.presentation.helpers.PostFilterOptions;
import com.example.bggforumproject.presentation.helpers.UserFilterOptions;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Repository
public class PostRepositoryImpl implements PostRepository {

    private static final List<String> VALID_CONDITIONS = List.of("<", "<=", ">", ">=", "=", "<>");
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final SessionFactory sessionFactory;

    @Autowired
    public PostRepositoryImpl(SessionFactory sessionFactory, UserRepository userRepository) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public List<Post> get(PostFilterOptions postFilterOptions) {
        try (Session session = sessionFactory.openSession()) {

            StringBuilder queryString = new StringBuilder("from Post p");
            if (postFilterOptions.getTags().isPresent()) {
                queryString.append(" join p.tags t");
            }

            if (postFilterOptions.getUserId().isPresent()) {
                queryString.append(" join p.userId u");
            }

            List<String> filters = new ArrayList<>();
            Map<String, Object> params = new HashMap<>();

            postFilterOptions.getUserId().ifPresent(value -> {

                filters.add("u.id = :userId");
                params.put("userId", value);
            });

            postFilterOptions.getTitle().ifPresent(value -> {
                filters.add("p.title like :title");
                params.put("title", String.format("%%%s%%", value));
            });

            postFilterOptions.getContent().ifPresent(value -> {
                filters.add("p.content like :content");
                params.put("content", String.format("%%%s%%", value));
            });

            postFilterOptions.getTags().ifPresent(value -> {
                List<Integer> tagIds = Arrays.stream(value.split(","))
                        .map(Integer::parseInt).toList();

                filters.add("t.id IN (:tagIds)");
                params.put("tagIds", tagIds);
            });

            postFilterOptions.getCreated().ifPresent(value -> {
                String condition = value.split(",")[0];
                String date = value.split(",")[1];

                if (VALID_CONDITIONS.contains(condition)) {
                    filters.add(String.format("p.createdAt %s (:created)", condition));
                    params.put("created", LocalDateTime.parse(date, FORMATTER));
                } else {
                    throw new InvalidFilterArgumentException("Filter condition not valid");
                }
            });

            postFilterOptions.getUpdated().ifPresent(value -> {
                String condition = value.split(",")[0];
                String date = value.split(",")[1];

                if (VALID_CONDITIONS.contains(condition)) {
                    filters.add(String.format("p.updatedAt %s (:updated)", condition));
                    params.put("updated", LocalDateTime.parse(date, FORMATTER));
                } else {
                    throw new InvalidFilterArgumentException("Filter condition not valid");
                }
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
    public List<Post> getMostCommented() {
        try (Session session = sessionFactory.openSession()) {
            Query<Post> query = session.createQuery("select p, count(c) as comment_count " +
                    "from Post p " +
                    "left join Comment c " +
                    "on c.postId.id = p.id " +
                    "group by p.id, p.title " +
                    "order by comment_count desc " +
                    "limit 10");

            return query.list();
        }
    }

    @Override
    public List<Post> getMostRecentlyCreated() {
        try (Session session = sessionFactory.openSession()) {
            Query<Post> query = session.createQuery("from Post order by createdAt desc limit 10");

            return query.list();
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
            return String.format(" order by u.id %s", determineSortOrder(postFilterOptions));
        }

        String orderBy = switch (postFilterOptions.getSortBy().get()) {
            case "title" -> "p.title";
            case "content" -> "p.content";
            case "user" -> "p.userId";
            case "created" -> "p.createdAt";
            case "yearCreated" -> "year(p.createdAt)";
            case "monthCreated" -> "month(p.createdAt)";
            case "dayCreated" -> "day(p.createdAt)";
            case "updated" -> "p.updatedAt";
            case "yearUpdated" -> "year(p.updatedAt)";
            case "monthUpdated" -> "month(p.updatedAt)";
            case "dayUpdated" -> "day(p.updatedAt)";
            default -> "p.id";
        };

        orderBy = String.format(" order by %s %s", orderBy, determineSortOrder(postFilterOptions));

        return orderBy;
    }

    private String determineSortOrder(PostFilterOptions postFilterOptions) {
        if (postFilterOptions.getSortOrder().isPresent() &&
                postFilterOptions.getSortOrder().get().equalsIgnoreCase("desc")) {
            return "desc";
        }
        return "";
    }
}
