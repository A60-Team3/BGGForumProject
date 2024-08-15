package com.example.bggforumproject.repositories;

import com.example.bggforumproject.exceptions.EntityNotFoundException;
import com.example.bggforumproject.exceptions.InvalidFilterArgumentException;
import com.example.bggforumproject.helpers.filters.PostFilterOptions;
import com.example.bggforumproject.models.Post;
import com.example.bggforumproject.repositories.contracts.PostRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public Page<Post> get(PostFilterOptions postFilterOptions, int pageIndex, int pageSize) {
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

            postFilterOptions.getPostIds().ifPresent(value -> {
                filters.add("p.id IN (:postIds)");
                params.put("postIds", value);
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
                filters.add("t.id IN (:tagIds)");
                params.put("tagIds", value);
            });

            postFilterOptions.getCreated().ifPresent(value -> {
                if (postFilterOptions.getCreatedCondition().isPresent()) {
                    filters.add(String.format("p.createdAt %s (:created)",
                            postFilterOptions.getCreatedCondition().get()));
                    params.put("created", value);
                } else {
                    throw new InvalidFilterArgumentException("Filter condition not valid");
                }
            });

            postFilterOptions.getUpdated().ifPresent(value -> {

                if (postFilterOptions.getUpdatedCondition().isPresent()) {
                    filters.add(String.format("p.updatedAt %s (:updated)",
                            postFilterOptions.getUpdatedCondition().get()));
                    params.put("updated", value);
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

            Pageable pageable = PageRequest.of(pageIndex, pageSize);
            int totalEntries = query.list().size();

            query.setFirstResult((int) pageable.getOffset());
            query.setMaxResults(pageSize);

            return new PageImpl<>(query.list(), pageable, totalEntries);
        }
    }

    @Override
    public List<Post> get() {
        try (Session session = sessionFactory.openSession()) {

            Query<Post> query = session.createQuery("from Post", Post.class);

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
    public Page<Post> getMostRecentlyCreated(int pageIndex, int pageSize) {
        try (Session session = sessionFactory.openSession()) {
            Query<Post> query = session.createQuery(
                    "from Post order by createdAt desc limit 10",
                    Post.class);

            Pageable pageable = PageRequest.of(pageIndex, pageSize);
            int totalEntries = query.list().size();

            query.setFirstResult((int) pageable.getOffset());
            query.setMaxResults(pageSize);

            return new PageImpl<>(query.list(), pageable, totalEntries);
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
    public void update(Post postToUpdate) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.merge(postToUpdate);
            session.getTransaction().commit();
        }
    }

    @Override
    public void delete(Post postToDelete) {
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
