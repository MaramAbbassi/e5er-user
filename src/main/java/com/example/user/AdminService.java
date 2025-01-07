package com.example.user;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.mindrot.jbcrypt.BCrypt;

@ApplicationScoped
public class AdminService {

    @Inject
    EntityManager em;

    @Transactional
    public void createUser(User user) {
        if (user.getUsername() == null || user.getEmail() == null || user.getPassword() == null) {
            throw new IllegalArgumentException("All fields are required.");
        }

        try {
            // Check if username or email already exists
            Long usernameCount = em.createQuery(
                            "SELECT COUNT(u) FROM User u WHERE u.username = :username", Long.class)
                    .setParameter("username", user.getUsername())
                    .getSingleResult();
            if (usernameCount > 0) {
                throw new IllegalArgumentException("Username already exists.");
            }

            Long emailCount = em.createQuery(
                            "SELECT COUNT(u) FROM User u WHERE u.email = :email", Long.class)
                    .setParameter("email", user.getEmail())
                    .getSingleResult();
            if (emailCount > 0) {
                throw new IllegalArgumentException("Email already exists.");
            }

            // Hash the password
            user.setPassword(BCrypt.hashpw(user.getPassword(), BCrypt.gensalt()));

            // Set role to "User" if not provided
            if (user.getRole() == null || user.getRole().isEmpty()) {
                user.setRole("User");
            }

            // Default LimCoins for new users
            user.setLimCoins(1000);

            // Persist the user
            em.persist(user);
        } catch (Exception e) {
            // Add debugging logs
            e.printStackTrace();
            throw new RuntimeException("Error during user registration: " + e.getMessage());
        }
    }

    @Transactional
    public void updateUser(Long id, User updatedUser) {
        User existingUser = em.find(User.class, id);
        if (existingUser == null) {
            throw new IllegalArgumentException("User not found.");
        }
        if (updatedUser.getUsername() != null) {
            existingUser.setUsername(updatedUser.getUsername());
        }
        if (updatedUser.getEmail() != null) {
            existingUser.setEmail(updatedUser.getEmail());
        }
        if (updatedUser.getPassword() != null) {
            existingUser.setPassword(updatedUser.getPassword());
        }
        em.merge(existingUser);
    }

    @Transactional
    public void deleteUser(Long id) {
        User user = em.find(User.class, id);
        if (user == null) {
            throw new IllegalArgumentException("User not found.");
        }
        em.remove(user);
    }
}
