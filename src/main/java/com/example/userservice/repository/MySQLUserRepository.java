package com.example.userservice.repository;

import com.example.userservice.domain.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import java.util.Optional;
import java.util.List;

public class MySQLUserRepository implements UserRepository {
    private final EntityManagerFactory emf;

    public MySQLUserRepository(EntityManagerFactory emf) {
        this.emf = emf;
    }

    @Override
    public void addUser(User user) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(user);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }


    @Override
    public Optional<User> getUserById(String id) {
        EntityManager em = emf.createEntityManager();
        try {
            User user = em.find(User.class, id);
            return Optional.ofNullable(user);
        } finally {
            em.close();
        }
    }

    @Override
    public List<User> getAllUsers() {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT u FROM User u", User.class)
                    .getResultList();
        } finally {
            em.close();
        }
    }
}
