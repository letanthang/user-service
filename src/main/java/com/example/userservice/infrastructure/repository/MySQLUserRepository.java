package com.example.userservice.infrastructure.repository;

import com.example.userservice.domain.entity.User;
import com.example.userservice.domain.repository.UserRepository;
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
    public Optional<User> getUserById(Integer id) {
        try (var em = emf.createEntityManager()) {
            User user = em.find(User.class, id);
            return Optional.ofNullable(user);
        }
    }

    @Override
    public Optional<User> getUserByEmail(String email) {
        try (EntityManager em = emf.createEntityManager()) {
            var results = em.createQuery("SELECT u FROM User u where u.email = :email", User.class)
                    .setParameter("email", email)
                    .getResultList();

            return results.isEmpty()?Optional.empty():Optional.of(results.get(0));
        }
    }

    @Override
    public List<User> getAllUsers() {
        try (EntityManager em = emf.createEntityManager()) {
            return em.createQuery("SELECT u FROM User u", User.class)
                    .getResultList();
        }
    }

    @Override
    public void addUser(User user) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            em.persist(user);
            em.getTransaction().commit();
        }
    }

    @Override
    public boolean deleteUser(Integer id) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            User user = em.find(User.class, id);
            if (user != null) {
                em.remove(user);
                em.getTransaction().commit();
                return true;
            }
            em.getTransaction().rollback();
            return false;
        }
    }

    @Override
    public void updateUser(User updatedUser) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            em.merge(updatedUser);
            em.getTransaction().commit();
        }
    }
}
