package com.example.user;

import jakarta.ws.rs.*;


import jakarta.ws.rs.*;


import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@RegisterRestClient(baseUri = "http://localhost:8085/Encheres") // URL de la ressource EnchereResource
@Path("/")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface EnchereRestClient {

    @POST
    @Path("/creteEnchereAleatoire")
    void createEnchereAleatoire();

    @POST
    @Path("/{userid}/{pokemonid}/{amount}")
    Long createEnchere(@PathParam("userid") Long userid, @PathParam("pokemonid") Long pokemonid, @PathParam("amount") double amount);

    @GET
    @Path("/{id}/{userId}/{Bid}")
    Response placerBid(@PathParam("id") Long id, @PathParam("userId") Long userId, @PathParam("Bid") double bid);

    @GET
    @Path("/Enchere/{id}")
    Enchere getEncherebyId(@PathParam("id") Long id);

    @GET
    List<Enchere> getAllEncheres();

    @GET
    @Path("/{type}")
    List<Enchere> getAllEncheresByType(@PathParam("type") String type);

    @GET
    @Path("/{id}")
    Response getEnchere(@PathParam("id") Long id);

    @POST
    @Path("/{pokemonId}/addAuctionHistory")
    Response addAuctionHistory(@PathParam("pokemonId") Long pokemonId, Enchere enchere);

    @POST
    @Path("{enchereid}/{userid}")
    Response EnleverBid(@PathParam("enchereid") Long enchereid, @PathParam("userid") Long userid);
}


