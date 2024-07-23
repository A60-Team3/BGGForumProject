package com.example.bggforumproject.persistance.repositories.impl;

import com.example.bggforumproject.persistance.models.Tag;
import com.example.bggforumproject.persistance.repositories.TagRepository;
import com.example.bggforumproject.presentation.exceptions.EntityNotFoundException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class TagRepositoryImpl implements TagRepository {

    private final SessionFactory sessionFactory;

    @Autowired
    public TagRepositoryImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public List<Tag> get() {
        try(Session session = sessionFactory.openSession()){
            Query<Tag> query = session.createQuery("from Tag", Tag.class);
            return query.list();
        }
    }

    @Override
    public Tag get(int id) {
        try(Session session = sessionFactory.openSession()){
            Tag tag = session.get(Tag.class, id);
            if(tag == null){
                throw new EntityNotFoundException("Tag", id);
            }
            return tag;
        }
    }

    @Override
    public Tag get(String name) {
        try(Session session = sessionFactory.openSession()){
            Query<Tag> query = session.createQuery("from Tag where name like :name", Tag.class);
            query.setParameter("name", name);

            List<Tag> result = query.list();
            if(result.isEmpty()){
                throw new EntityNotFoundException("Tag", "name", name);
            }
            return result.get(0);
        }
    }

    @Override
    public void create(Tag tag) {
        try(Session session = sessionFactory.openSession()){
            session.beginTransaction();
            session.persist(tag);
            session.getTransaction().commit();
        }
    }

    @Override
    public void update(Tag tag) {
        try(Session session = sessionFactory.openSession()){
            session.beginTransaction();
            session.merge(tag);
            session.getTransaction().commit();
        }
    }

    @Override
    public void delete(int id) {
        Tag tagToDelete = get(id);
        try(Session session = sessionFactory.openSession()){
            session.beginTransaction();
            session.remove(tagToDelete);
            session.getTransaction().commit();
        }
    }
}
