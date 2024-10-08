package com.example.bggforumproject.repositories;

import com.example.bggforumproject.models.Role;
import com.example.bggforumproject.models.enums.RoleType;
import com.example.bggforumproject.repositories.contracts.RoleRepository;
import com.example.bggforumproject.exceptions.EntityNotFoundException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class RoleRepositoryImpl implements RoleRepository {

    private final SessionFactory sessionFactory;

    public RoleRepositoryImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public Role getByAuthority(RoleType authority) {
        try (Session session = sessionFactory.openSession()) {

            Query<Role> query = session.createQuery("from Role where authority = :authority", Role.class);
            query.setParameter("authority", authority);

            List<Role> list = query.list();
            if (list.isEmpty()){
                throw new EntityNotFoundException("Role", "role", authority.name());
            }

            return list.get(0);
        }
    }

    @Override
    public void create(Role role) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.persist(role);
            session.getTransaction().commit();
        }
    }

    @Override
    public List<Role> findAll() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("from Role", Role.class).list();
        }
    }
}
