package com.example.userservice.repository;

import com.example.userservice.domain.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.util.Optional;

public class MySQLUserRepository implements UserRepository {
    private final EntityManagerFactory emf = Persistence.createEntityManagerFactory("default");

    @Override
    public void addUser(User user) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        em.persist(user);
        em.getTransaction().commit();
        em.close();
    }

    @Override
    public Optional<User> getUserById(String id) {
        EntityManager em = emf.createEntityManager();
        User user = em.find(User.class, id);
        em.close();
        return Optional.ofNullable(user);
    }
}
