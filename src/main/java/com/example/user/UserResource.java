package com.example.user;

import com.example.user.Exception.UserNotFoundException;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;

import java.util.List;

@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserResource {

    @Inject
    UserService userService;

    @Context
    SecurityContext securityContext;

    @GET
    @RolesAllowed("Admin") // Only Admins can access the list of all users
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GET
    @Path("/{id}")
    @RolesAllowed({"User", "Admin"}) // Both Users and Admins can access this
    public User getUserById(@PathParam("id") Long id) {
        return userService.findUserById(id);
    }

    @POST
    @RolesAllowed("Admin") // Only Admins can add new users
    public Response addUser(User user) {
        userService.addUser(user);
        return Response.status(Response.Status.CREATED).entity(user).build();
    }

    @PUT
    @Path("/{id}")
    @RolesAllowed("Admin") // Only Admins can update users
    public Response updateUser(@PathParam("id") Long id, User user) {
        try {
            userService.updateUser(id, user, securityContext.isUserInRole("Admin") ? "Admin" : "User");
            return Response.ok("User updated successfully!").build();
        } catch (UserNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
        } catch (SecurityException e) {
            return Response.status(Response.Status.FORBIDDEN).entity(e.getMessage()).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("An unexpected error occurred: " + e.getMessage())
                    .build();
        }
    }

    @POST
    @Path("/{id}/add-mcoins")
    @RolesAllowed({"User", "Admin"}) // Both Users and Admins can add coins
    public Response addLimCoins(@PathParam("id") Long id, @QueryParam("amount") int amount) {
        if (!userService.addLimCoins(id, amount)) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("User not found.")
                    .build();
        }
        return Response.ok("Coins added successfully!").build();
    }

    @POST
    @Path("/{id}/deduct-limcoins")
    @RolesAllowed({"User", "Admin"}) // Both Users and Admins can spend coins
    public Boolean deductLimCoins(@PathParam("id") Long id, @QueryParam("amount") int amount) {
        return userService.deductLimCoins(id, amount);
    }

    @POST
    @Path("/{userId}/add-pokemon")
    public Response addPokemonToUser(@PathParam("userId") Long userId, Long pokemonId) {
        userService.addPokemonToUser(userId, pokemonId);
        return Response.ok("Pokemon added successfully!").build();
    }

    @GET
    @Path("/{userId}/pokemons")
    public Response getUserPokemons(@PathParam("userId") Long userId) {
        return Response.ok(userService.getUserPokemons(userId)).build();
    }

    @POST
    @Path("/{userId}/place-bid/{enchereId}")
    public Response placeBid(@PathParam("userId") Long userId, @PathParam("enchereId") Long enchereId, @QueryParam("amount") double amount) {
        userService.placeBid(userId, enchereId, amount);
        return Response.ok("Bid placed successfully!").build();
    }

    @POST
    @Path("/{userId}/abandon-bid/{bidId}")
    public Response abandonBid(@PathParam("userId") Long userId, @PathParam("bidId") Long bidToAbandonId) {
        String result = userService.abandonBid(userId, bidToAbandonId);
        return Response.ok(result).build();
    }

    @POST
    @Path("/{userId}/create-enchere")
    public Response createEnchere(@PathParam("userId") Long userId, @QueryParam("pokemonId") Long pokemonId, @QueryParam("startingPrice") double startingPrice) {
        String result = userService.createEnchere(userId, pokemonId, startingPrice);
        return Response.ok(result).build();
    }

    @GET
    @Path("/{userId}/bids")
    public Response getUserEncheres(@PathParam("userId") Long userId) {
        return Response.ok(userService.getUserEncheres(userId)).build();
    }

    @POST
    @Path("/{userId}/sell-pokemon/{pokemonId}")
    @RolesAllowed({"User", "Admin"}) // Both Users and Admins can sell Pokémon
    public Response sellPokemonToSystem(@PathParam("userId") Long userId, @PathParam("pokemonId") Long pokemonId) {
        try {
            String result = userService.sellPokemonToSystem(userId, pokemonId);
            return Response.ok(result).build();
        } catch (UserNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("An unexpected error occurred: " + e.getMessage())
                    .build();
        }
    }

    @GET
    @Path("/top-limcoins")
    @RolesAllowed("Admin") // Restrict access to Admins
    public Response getTopUsersByLimCoins() {
        try {
            List<User> topUsers = userService.getTopUsersByLimCoins();
            return Response.ok(topUsers).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("An unexpected error occurred: " + e.getMessage())
                    .build();
        }
    }

    @POST
    @Path("/{userId}/encheres/active/{enchereId}")
    @RolesAllowed({"User", "Admin"}) // Both Users and Admins can add active encheres
    public Response addEnchereToActive(@PathParam("userId") Long userId, @PathParam("enchereId") Long enchereId) {
        try {
            userService.addEnchereToActive(userId, enchereId);
            return Response.ok("Enchere added to active list successfully!").build();
        } catch (UserNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("An unexpected error occurred: " + e.getMessage())
                    .build();
        }
    }

    @POST
    @Path("/{userId}/encheres/general/{enchereId}")
    @RolesAllowed({"User", "Admin"}) // Both Users and Admins can add general encheres
    public Response addEnchere(@PathParam("userId") Long userId, @PathParam("enchereId") Long enchereId) {
        try {
            userService.addEnchere(userId, enchereId);
            return Response.ok("Enchere added to general list successfully!").build();
        } catch (UserNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("An unexpected error occurred: " + e.getMessage())
                    .build();
        }
    }


}
