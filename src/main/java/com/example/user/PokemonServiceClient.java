package com.example.user;

import jakarta.ws.rs.*;



import jakarta.ws.rs.*;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@RegisterRestClient(configKey = "pokemon-service")
@Path("/pokemons")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface PokemonServiceClient {

    @GET
    List<Pokemon> listerPokemons();

    @GET
    @Path("/random")
    Pokemon pokemonAleatoire();

    @POST
    Pokemon creerPokemon(Pokemon pokemon);

    @GET
    @Path("/{id}")
    Pokemon trouverPokemon(@PathParam("id") Long id);

    @DELETE
    @Path("/{id}")
    void supprimerPokemon(@PathParam("id") Long id);

    @POST
    @Path("/{pokemonId}/addAuction")
    Response addAuctionHistory(@PathParam("pokemonId") Long pokemonId, Enchere enchere);
}
