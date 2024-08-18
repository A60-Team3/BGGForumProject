package com.example.bggforumproject.repositories;

import com.example.bggforumproject.models.ProfilePicture;
import com.example.bggforumproject.repositories.contracts.PictureRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class PictureRepositoryImpl implements PictureRepository {

    private final SessionFactory sessionFactory;

    public PictureRepositoryImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public List<ProfilePicture> getAll() {
        try (Session session = sessionFactory.openSession()) {
            Query<ProfilePicture> query = session.createQuery("from ProfilePicture ", ProfilePicture.class);
            return query.list();
        }
    }

    @Override
    public ProfilePicture get(String userId) {
        try (Session session = sessionFactory.openSession()) {
            Query<ProfilePicture> query = session.createQuery("from ProfilePicture where photoUrl = :url", ProfilePicture.class);

            return query.setParameter("url", userId).getSingleResultOrNull();
        }
    }

    @Override
    public void savePhoto(ProfilePicture profilePicture) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.persist(profilePicture);
            session.getTransaction().commit();
        }
    }

    @Override
    public void updatePhoto(ProfilePicture profilePicture) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.merge(profilePicture);
            session.getTransaction().commit();
        }
    }

    @Override
    public void deletePhoto(ProfilePicture profilePicture) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.remove(profilePicture);
            session.getTransaction().commit();
        }
    }
}
