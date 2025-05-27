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
    public Optional<User> getUserById(Integer id) {
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

    @Override
    public boolean deleteUser(Integer id) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            User user = em.find(User.class, id);
            if (user != null) {
                em.remove(user);
                em.getTransaction().commit();
                return true;
            }
            em.getTransaction().rollback();
            return false;
        } finally {
            em.close();
        }
    }

    @Override
    public boolean updateUser(Integer id, User updatedUser) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            User existingUser = em.find(User.class, id);
            if (existingUser != null) {
                existingUser.setName(updatedUser.getName());
                existingUser.setEmail(updatedUser.getEmail());
                existingUser.setGender(updatedUser.getGender());
                existingUser.setNickname(updatedUser.getNickname());
                existingUser.setAvatar(updatedUser.getAvatar());
                existingUser.setBirthdate(updatedUser.getBirthdate());
                em.getTransaction().commit();
                return true;
            }
            em.getTransaction().rollback();
            return false;
        } finally {
            em.close();
        }
    }
}
