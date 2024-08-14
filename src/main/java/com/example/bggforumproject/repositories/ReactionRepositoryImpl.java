package com.example.bggforumproject.repositories;

import com.example.bggforumproject.models.Reaction;
import com.example.bggforumproject.repositories.contracts.ReactionRepository;
import com.example.bggforumproject.exceptions.EntityNotFoundException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ReactionRepositoryImpl implements ReactionRepository {

    private final SessionFactory sessionFactory;

    @Autowired
    public ReactionRepositoryImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public List<Reaction> getAll(long postId) {
        try (Session session = sessionFactory.openSession()) {
            Query<Reaction> query = session.createQuery(
                    "from Reaction where postId.id = :postId",
                    Reaction.class);
            query.setParameter("postId", postId);

            return query.list();
        }
    }

    @Override
    public int getLikesCount(long postId) {
        try (Session session = sessionFactory.openSession()) {
            Query<Integer> query = session.createQuery(
                    "select count(reactionType) from Reaction where postId.id = :postId and reactionType = 'LIKE'");
            query.setParameter("postId", postId);

            return query.list().get(0);
        }
    }

    @Override
    public int getDislikesCount(long postId) {
        try (Session session = sessionFactory.openSession()) {
            Query<Integer> query = session.createQuery(
                    "select count(reactionType) from Reaction where postId.id = :postId and reactionType = 'DISLIKE'");
            query.setParameter("postId", postId);

            return query.list().get(0);
        }
    }

    @Override
    public Reaction get(long id) {
        try (Session session = sessionFactory.openSession()) {
            Reaction reaction = session.get(Reaction.class, id);

            if (reaction == null) {
                throw new EntityNotFoundException("Reaction", id);
            }

            return reaction;
        }
    }

    @Override
    public Reaction getByPostAndUser(long userId, long postId) {
        try (Session session = sessionFactory.openSession()) {
            Query<Reaction> query = session.createQuery("from Reaction " +
                    "where postId.id = :postId " +
                    "and userId.id = :userId", Reaction.class);

            query.setParameter("postId", postId);
            query.setParameter("userId", userId);

            List<Reaction> result = query.list();
            if (result.isEmpty()) {
                throw new EntityNotFoundException("Reaction", "userId", String.valueOf(userId));
            }

            return result.get(0);
        }
    }

    @Override
    public void create(Reaction reaction) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.persist(reaction);
            session.getTransaction().commit();
        }
    }

    @Override
    public void update(Reaction reaction) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.merge(reaction);
            session.getTransaction().commit();
        }
    }

    @Override
    public void delete(Reaction reactionToDelete) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.remove(reactionToDelete);
            session.getTransaction().commit();
        }
    }
}
