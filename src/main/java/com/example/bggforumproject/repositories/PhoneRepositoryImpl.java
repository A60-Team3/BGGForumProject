package com.example.bggforumproject.repositories;

import com.example.bggforumproject.models.PhoneNumber;
import com.example.bggforumproject.repositories.contracts.PhoneRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class PhoneRepositoryImpl implements PhoneRepository {
    private final SessionFactory sessionFactory;

    public PhoneRepositoryImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public List<PhoneNumber> getAll() {
        try (Session session = sessionFactory.openSession()) {
            Query<PhoneNumber> query =
                    session.createQuery("FROM PhoneNumber ", PhoneNumber.class);
            return query.list();
        }
    }

    @Override
    public PhoneNumber get(long userId) {
        try (Session session = sessionFactory.openSession()){
            Query<PhoneNumber> query =
                    session.createQuery("FROM PhoneNumber WHERE user.id = :userId", PhoneNumber.class);
                query.setParameter("userId", userId);
            return query.getSingleResultOrNull();
        }
    }

    @Override
    public void savePhone(PhoneNumber phoneNumber) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.persist(phoneNumber);
            session.getTransaction().commit();
        }
    }

    @Override
    public void updatePhone(PhoneNumber phoneNumber) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.merge(phoneNumber);
            session.getTransaction().commit();
        }
    }

    @Override
    public void deletePhone(PhoneNumber phoneNumber) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.remove(phoneNumber);
            session.getTransaction().commit();
        }
    }
}
