package com.example.utils;

import io.smallrye.jwt.build.Jwt;
import java.util.Set;

public class JwtUtils {

    // Generates a JWT token for a user
    public static String generateToken(String username, String role) {
        return Jwt.subject(username)
                .groups(Set.of(role)) // Default group
                .expiresIn(3600)        // Token expires in 1 hour
                .sign();                // Signs the JWT with Quarkus' default secret key
    }
}
