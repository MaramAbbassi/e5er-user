package com.example.user;





import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(columnNames = "username"),
        @UniqueConstraint(columnNames = "email")
})
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(nullable = false, unique = true)
    private String username;

    @NotNull
    @Email
    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private int limCoins;

    @Column(nullable = false)
    private String role = "User";

    @ElementCollection
    @CollectionTable(name = "pokemon-user", joinColumns = @JoinColumn(name = "user-id"))
    @Column(name = "pokemon")
    private List<Long> pokemons = new ArrayList<>();

    //liste des encheres actives

    @ElementCollection
    @CollectionTable(name = "active_encheres", joinColumns = @JoinColumn(name = "user-id"))
    @Column(name = "encheres")
    private List<Long> encheres = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "encher_user", joinColumns = @JoinColumn(name = "user-id"))
    @Column(name = "encheresDeUser")
    private List<Long> encheresDeUser=new ArrayList<>();


    public List<Long> getPokemons() {
        return pokemons;
    }

    public void setPokemons(List<Long> pokemons) {
        this.pokemons = pokemons;
    }

    public List<Long> getEncheres() {
        return encheres;
    }

    public void setEncheres(List<Long> encheres) {
        this.encheres = encheres;
    }

    public List<Long> getEncheresDeUser() {
        return encheresDeUser;
    }

    public void setEncheresDeUser(List<Long> encheresDeUser) {
        this.encheresDeUser = encheresDeUser;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }


    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getLimCoins() {
        return limCoins;
    }

    public void setLimCoins(int limCoins) {
        this.limCoins = limCoins;
    }
}
