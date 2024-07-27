package com.example.bggforumproject.repositories;

import com.example.bggforumproject.exceptions.InvalidFilterArgumentException;
import com.example.bggforumproject.helpers.filters.PostFilterOptions;
import com.example.bggforumproject.helpers.filters.TagFilterOptions;
import com.example.bggforumproject.models.Post;
import com.example.bggforumproject.models.Tag;
import com.example.bggforumproject.repositories.contracts.TagRepository;
import com.example.bggforumproject.exceptions.EntityNotFoundException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.*;

@Repository
public class TagRepositoryImpl implements TagRepository {

    private final SessionFactory sessionFactory;

    @Autowired
    public TagRepositoryImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
    //TODO remove bidirectional with posts
    @Override
    public List<Tag> get(TagFilterOptions tagFilterOptions) {
        try (Session session = sessionFactory.openSession()) {
            StringBuilder queryString = new StringBuilder("from Tag t");

            if (tagFilterOptions.getPostIds().isPresent()) {
                queryString.append(" join t.posts p");
            }

            List<String> filters = new ArrayList<>();
            Map<String, Object> params = new HashMap<>();

            tagFilterOptions.getTagId().ifPresent(value -> {
                filters.add("t.id = :tagId");
                params.put("tagId", value);
            });

            tagFilterOptions.getName().ifPresent(value -> {
                filters.add("t.name like :name");
                params.put("name", String.format("%%%s%%", value.trim().toLowerCase()));
            });

            tagFilterOptions.getPostIds().ifPresent(value -> {
                List<Long> postIds = Arrays.stream(value.split(","))
                        .map(Long::parseLong).toList();

                filters.add("p.id IN (:postIds)");
                params.put("postIds", postIds);
            });

            if (!filters.isEmpty()) {
                queryString.append(" where ")
                        .append(String.join(" and ", filters));
            }

            queryString.append(generateOrderBy(tagFilterOptions));

            Query<Tag> query = session.createQuery(queryString.toString(), Tag.class);
            query.setProperties(params);

            return query.list();
        }
    }

    @Override
    public Tag get(long id) {
        try (Session session = sessionFactory.openSession()) {
            Tag tag = session.get(Tag.class, id);
            if (tag == null) {
                throw new EntityNotFoundException("Tag", id);
            }
            return tag;
        }
    }

    @Override
    public Tag get(String name) {
        try (Session session = sessionFactory.openSession()) {
            Query<Tag> query = session.createQuery("from Tag where name like :name", Tag.class);
            query.setParameter("name", name.toLowerCase());

            List<Tag> result = query.list();
            if (result.isEmpty()) {
                throw new EntityNotFoundException("Tag", "name", name);
            }
            return result.get(0);
        }
    }

    @Override
    public List<Tag> getTagsOfPost(long postId) {
        try (Session session = sessionFactory.openSession()) {
            Query<Tag> query = session.createQuery(
                    "select t from Post p " +
                            "join p.tags t " +
                            "where p.id = :postId",
                    Tag.class);
            query.setParameter("postId", postId);
            return query.list();
        }
    }

    @Override
    public void create(Tag tag) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.persist(tag);
            session.getTransaction().commit();
        }
    }


    @Override
    public void update(Tag tag) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.merge(tag);
            session.getTransaction().commit();
        }
    }


    @Override
    public void delete(long id) {
        Tag tagToDelete = get(id);
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.remove(tagToDelete);
            session.getTransaction().commit();
        }
    }

    private String generateOrderBy(TagFilterOptions tagFilterOptions) {

        String orderBy = tagFilterOptions.getSortBy().isEmpty() ? "t.id" : "t.name";
        String sortOrder = determineSortOrder(tagFilterOptions);

        return String.format(" order by %s %s", orderBy, sortOrder);
    }

    private String determineSortOrder(TagFilterOptions tagFilterOptions) {
        if (tagFilterOptions.getSortOrder().isPresent() &&
                tagFilterOptions.getSortOrder().get().equalsIgnoreCase("desc")) {
            return "desc";
        }
        return "";
    }
}
