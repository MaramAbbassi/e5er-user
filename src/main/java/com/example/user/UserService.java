package com.example.user;

import com.example.user.Exception.UserNotFoundException;
import com.example.utils.JwtUtils;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

//import org.mindrot.jbcrypt.BCrypt;
//import com.example.utils.JwtUtils;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.mindrot.jbcrypt.BCrypt;


@ApplicationScoped


public class UserService {

    @Inject
    EntityManager em;

    @Inject
    @RestClient
    PokemonServiceClient pokemonClient;

    @Inject
    @RestClient
    EnchereRestClient enchereClient;


    public List<User> getAllUsers() {
        return em.createQuery("SELECT u FROM User u", User.class).getResultList();
    }

    public User findUserById(Long id) {
        User user = em.find(User.class, id);
        if (user == null) {
            throw new UserNotFoundException("User with ID " + id + " not found.");
        }

        List<Long> pokemons = user.getPokemons();
        List<Long> encheres = user.getEncheres();
        user.setPokemons(pokemons);
        user.setEncheres(encheres);

        return user;
    }

    @Transactional
    public void addUser(User user) {
        // Validate required fields
        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            throw new IllegalArgumentException("Username is required.");
        }
        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email is required.");
        }
        if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("Password is required.");
        }

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

        try {
            String hashedPassword = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());
            user.setPassword(hashedPassword);
        } catch (Exception e) {
            throw new RuntimeException("Error hashing password: " + e.getMessage(), e);
        }

        if (user.getRole() == null || user.getRole().isEmpty()) {
            user.setRole("User");
        }

        if (user.getLimCoins() == 0) {
            user.setLimCoins(1000);
        }

        em.persist(user);
    }


    @Transactional
    public void updateUser(Long id, User updatedUser, String authenticatedRole) {
        if (!"Admin".equals(authenticatedRole)) {
            throw new SecurityException("Only Admins can update users.");
        }

        User existingUser = findUserById(id);
        if (existingUser == null) {
            throw new UserNotFoundException("Cannot update: User not found.");
        }

        if (updatedUser.getUsername() != null) {
            existingUser.setUsername(updatedUser.getUsername());
        }
        if (updatedUser.getEmail() != null) {
            existingUser.setEmail(updatedUser.getEmail());
        }
        if (updatedUser.getRole() != null) {
            existingUser.setRole(updatedUser.getRole());
        }

        em.merge(existingUser);
    }

    @Transactional
    public void deleteUser(Long id, String authenticatedRole) {
        if (!"Admin".equals(authenticatedRole)) {
            throw new SecurityException("Only Admins can delete users.");
        }

        User user = findUserById(id);
        if (user == null) {
            throw new UserNotFoundException("Cannot delete: User not found.");
        }

        em.remove(user);
    }


    @Transactional
    public void registerUser(User user) {
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



    public String loginUser(String username, String password) {
        try {
            // Normalize input username by trimming spaces
            if (username == null || username.trim().isEmpty()) {
                throw new IllegalArgumentException("Username cannot be empty.");
            }
            if (password == null || password.trim().isEmpty()) {
                throw new IllegalArgumentException("Password cannot be empty.");
            }


            // Query the database for the user by username
            User user = em.createQuery("SELECT u FROM User u WHERE u.username = :username", User.class)
                    .setParameter("username", username)
                    .getSingleResult();

            // Validate the password using BCrypt
            if (!BCrypt.checkpw(password, user.getPassword())) {
                throw new IllegalArgumentException("Invalid password.");
            }

            // Generate and return the JWT token
            return  JwtUtils.generateToken(user.getUsername(), user.getRole());

        } catch (NoResultException e) {
            throw new IllegalArgumentException("User not found with the provided username.");
        }
    }

    @Transactional
    public boolean addLimCoins(Long userId, int amount) {
        User user = findUserById(userId);
        if (user == null) {
            return false; // User not found
        }
        user.setLimCoins(user.getLimCoins() + amount);
        em.merge(user);
        return true; // Coins added successfully
    }


    @Transactional
    public boolean deductLimCoins(Long userId, int amount) {
        User user = findUserById(userId);
        if (user == null || user.getLimCoins() < amount) {
            return false; // User not found or insufficient coins
        }
        user.setLimCoins(user.getLimCoins() - amount);
        em.merge(user);
        return true; // Coins deducted successfully
    }

    private void checkForDuplicateUser(User user) {
        Long usernameCount = em.createQuery(
                        "SELECT COUNT(u) FROM User u WHERE u.username = :username AND u.id != :id", Long.class)
                .setParameter("username", user.getUsername())
                .setParameter("id", user.getId())
                .getSingleResult();

        if (usernameCount > 0) {
            throw new IllegalArgumentException("Username already exists.");
        }

        Long emailCount = em.createQuery(
                        "SELECT COUNT(u) FROM User u WHERE u.email = :email AND u.id != :id", Long.class)
                .setParameter("email", user.getEmail())
                .setParameter("id", user.getId())
                .getSingleResult();

        if (emailCount > 0) {
            throw new IllegalArgumentException("Email already exists.");
        }
    }

    @Transactional
    public void addPokemonToUser(Long userId, Long pokemonid) {
        User user = findUserById(userId);

        user.getPokemons().add(pokemonid);
        em.merge(user);
    }

    public List<Long> getUserPokemons(Long userId) {
        return findUserById(userId).getPokemons();
    }


    public List<Long> getUserEncheres(Long userId) {
        return findUserById(userId).getEncheres();
    }

    @Transactional
    public String sellPokemonToSystem(Long userId, Long pokemonId) {
        // Find the user
        User user = findUserById(userId);
        if (user == null) {
            throw new UserNotFoundException("User not found.");
        }

        // Check if the user owns the Pokémon
        Long pokemonToSell = user.getPokemons().stream()
                .filter(pokemon -> pokemon.equals(pokemonId))
                .findFirst()
                .orElse(null);

        if (pokemonToSell == null) {
            throw new IllegalArgumentException("User does not own this Pokémon.");
        }

        Pokemon pokemon = pokemonClient.trouverPokemon(pokemonToSell);
        double pokemonRealValue = pokemon.getValeurReelle();

        user.setLimCoins(user.getLimCoins() + (int) pokemonRealValue);

        user.getPokemons().remove(pokemonToSell);

        em.merge(user);

        return "Pokémon sold successfully! Real value: " + pokemonRealValue + " LimCoins.";
    }

    public List<User> getTopUsersByLimCoins() {
        return em.createQuery("SELECT u FROM User u ORDER BY u.limCoins DESC", User.class)
                .setMaxResults(5) // Limit the results to 5
                .getResultList();
    }

    //get ecnhere by user id
    public List<Long> getEnchereByuserId(Long userId) {
        User user = findUserById(userId);
        return user.getEncheres();
    }

    @Transactional
    public void placeBid(Long userId, Long enchereId, double amount) {
        User user = findUserById(userId);
        Enchere enchere = enchereClient.getEncherebyId(enchereId);
        enchereClient.placerBid(userId, enchereId, amount); // Notify the Enchère microservice
        addEnchereToActive(userId, enchereId);
        em.merge(user);
    }

    @Transactional
    public String abandonBid(Long userId, Long enchereId) {
        // Find the user
        User user = findUserById(userId);
        if (user == null) {
            throw new IllegalArgumentException("User with ID " + userId + " not found.");
        }

        // Check if the user has the specified auction in their active bids
        if (!user.getEncheres().contains(enchereId)) {
            return "Auction with ID " + enchereId + " not found in user's active bids.";
        }

        try {
            // Call the Enchere microservice to remove the user's bid from the auction
            enchereClient.enleverBid(enchereId, userId);

            // Remove the auction ID from the user's active bids list
            boolean removed = user.getEncheres().remove(enchereId);

            // Persist the updated user entity
            em.merge(user);

            // Return success or failure based on the removal result
            return removed
                    ? "Bid abandoned successfully from auction ID " + enchereId + "."
                    : "Failed to abandon bid from auction ID " + enchereId + ".";
        } catch (Exception e) {
            // Handle any exceptions and provide feedback
            throw new RuntimeException("Error abandoning bid from auction ID " + enchereId + ": " + e.getMessage(), e);
        }
    }



    @Transactional
    public String createEnchere(Long userId, Long pokemonId, double startingPrice) {
        User user = findUserById(userId);
        Long createdEnchereId = enchereClient.createEnchere(userId, pokemonId, startingPrice);
        user.getEncheresDeUser().add(createdEnchereId);
        em.merge(user);
        return "Enchere created successfully with ID: " + createdEnchereId;
    }

    /*
    @Transactional
    public String deleteEnchere(Long userId, Long enchereId) {
        User user = findUserById(userId);
        enchereClient.deleteEnchere(enchereId);
        boolean removed = user.getEncheresDeUser().remove(enchereId);
        em.merge(user);
        return removed ? "Enchere deleted successfully." : "Enchere not found in user's active encheres.";
    }
    */


    @Transactional
    public void addEnchereToActive(Long userId, Long enchereId) {
        User user = findUserById(userId);
        if (user == null) {
            throw new UserNotFoundException("User with ID " + userId + " not found.");
        }

        if (!user.getEncheres().contains(enchereId)) {
            user.getEncheres().add(enchereId);
            em.merge(user);
        } else {
            throw new IllegalArgumentException("Enchere ID " + enchereId + " is already in the user's active encheres list.");
        }
    }

    @Transactional
    public void addEnchere(Long userId, Long enchereId) {
        User user = findUserById(userId);
        if (user == null) {
            throw new UserNotFoundException("User with ID " + userId + " not found.");
        }

        if (!user.getEncheresDeUser().contains(enchereId)) {
            user.getEncheresDeUser().add(enchereId);
            em.merge(user);
        } else {
            throw new IllegalArgumentException("Enchere ID " + enchereId + " is already in the user's encheres list.");
        }
    }


   /* public double calculateTotalWonBids(Long userId) {
        User user = findUserById(userId);

        return user.getEncheres().stream()
                .filter(bid -> bid.getEnchere().getStatus().equals("closed") &&
                        bid.getEnchere().getHighestBidderId().equals(userId))
                .mapToDouble(Bid::getAmount)
                .sum();
    }*/


}


