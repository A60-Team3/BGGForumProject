package com.example.bggforumproject.persistance.repositories.impl;

import com.example.bggforumproject.persistance.models.Post;
import com.example.bggforumproject.persistance.repositories.PostRepository;
import com.example.bggforumproject.presentation.exceptions.EntityNotFoundException;
import com.example.bggforumproject.presentation.exceptions.InvalidFilterArgumentException;
import com.example.bggforumproject.presentation.helpers.PostFilterOptions;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Repository
public class PostRepositoryImpl implements PostRepository {

    private static final List<String> VALID_CONDITIONS = List.of("<", "<=", ">", ">=", "=", "<>");
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final SessionFactory sessionFactory;

    @Autowired
    public PostRepositoryImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public List<Post> get(PostFilterOptions postFilterOptions) {
        try (Session session = sessionFactory.openSession()) {

            StringBuilder queryString = new StringBuilder("from Post p");
            if (postFilterOptions.getTags().isPresent()) {
                queryString.append(" join p.tags t");
            }

            if (postFilterOptions.getUserId().isPresent() ||
                    (postFilterOptions.getSortBy().isPresent()
                            && postFilterOptions.getSortBy().get().equals("user"))) {
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
                params.put("title", String.format("%%%s%%", value.trim()));
            });

            postFilterOptions.getContent().ifPresent(value -> {
                filters.add("p.content like :content");
                params.put("content", String.format("%%%s%%", value.trim()));
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
                    params.put("created", LocalDateTime.parse(date.trim(), FORMATTER));
                } else {
                    throw new InvalidFilterArgumentException("Filter condition not valid");
                }
            });

            postFilterOptions.getUpdated().ifPresent(value -> {
                String condition = value.split(",")[0];
                String date = value.split(",")[1];

                if (VALID_CONDITIONS.contains(condition)) {
                    filters.add(String.format("p.updatedAt %s (:updated)", condition));
                    params.put("updated", LocalDateTime.parse(date.trim(), FORMATTER));
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

            if (query.list().isEmpty()){
                throw new EntityNotFoundException("No posts satisfy applied conditions");
            }

            return query.list();
        }
    }

    @Override
    public Post get(long id) {
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
            if (result.isEmpty()) {
                throw new EntityNotFoundException("Post", "title", title);
            }

            return result.get(0);
        }
    }

    @Override
    public List<Post> getMostCommented() {
        try (Session session = sessionFactory.openSession()) {
            Query<Post> query = session.createQuery(
                    "from Post p " +
                            "left join Comment c " +
                            "on c.postId.id = p.id " +
                            "group by p.id, p.title " +
                            "order by count(c) desc " +
                            "limit 10",
                    Post.class
            );

            return query.list();
        }
    }

    @Override
    public List<Post> getMostRecentlyCreated() {
        try (Session session = sessionFactory.openSession()) {
            Query<Post> query = session.createQuery(
                    "from Post order by createdAt desc limit 10",
                    Post.class);
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
    public void delete(long id) {
        Post postToDelete = get(id);
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.remove(postToDelete);
            session.getTransaction().commit();
        }
    }

    private String generateOrderBy(PostFilterOptions postFilterOptions) {
        if (postFilterOptions.getSortBy().isEmpty()) {
            return String.format(" order by p.id %s", determineSortOrder(postFilterOptions));
        }

        String orderBy = switch (postFilterOptions.getSortBy().get().trim()) {
            case "title" -> "p.title";
            case "content" -> "p.content";
            case "user" -> "CONCAT_WS(u.firstName,' ', u.lastName)";
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
