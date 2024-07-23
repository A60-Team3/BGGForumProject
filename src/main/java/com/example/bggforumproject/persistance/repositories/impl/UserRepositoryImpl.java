package com.example.bggforumproject.persistance.repositories.impl;

import com.example.bggforumproject.persistance.models.User;
import com.example.bggforumproject.persistance.repositories.UserRepository;
import com.example.bggforumproject.presentation.exceptions.EntityNotFoundException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UserRepositoryImpl implements UserRepository {

    private final SessionFactory sessionFactory;

    public UserRepositoryImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public User findByUsername(String username) {
        try (Session session = sessionFactory.openSession()) {
            Query<User> query = session.createQuery("from User where username = :username", User.class);
            query.setParameter("username", username);
            User user = query.list().get(0);
            if (user == null) {
                throw new EntityNotFoundException("User", "username", username);
            }
            return user;
        }

    }

    @Override
    public User findByEmail(String email) {
        try (Session session = sessionFactory.openSession()) {
            Query<User> query = session.createQuery("from User where email = :email", User.class);
            query.setParameter("email", email);
            User user = query.list().get(0);
            if (user == null) {
                throw new EntityNotFoundException("User", "email", email);
            }
            return user;
        }
    }

    @Override
    public User findById(long id) {
        try (Session session = sessionFactory.openSession()) {
            User user = session.get(User.class, id);
            if (user == null) {
                throw new EntityNotFoundException("User",id);
            }
            return user;
        }
    }

    @Override
    public List<User> findAll(){
        try(Session session = sessionFactory.openSession()){
            return session.createQuery("from User ", User.class).list();
        }
    }

    @Override
    public User create(User user) {

        try(Session session = sessionFactory.openSession()){
            session.beginTransaction();
            session.persist(user);
            session.getTransaction().commit();
        }

        return findByEmail(user.getEmail());
    }
}
