package com.example.user;

import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/admin")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RolesAllowed("Admin") // Ensure only admins can access these endpoints
public class AdminResource {

    @Inject
    AdminService adminService;

    @POST
    @Path("/create")
    public Response createUser(User user) {
        try {
            adminService.createUser(user);
            return Response.status(Response.Status.CREATED).entity("User created successfully!").build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    @PUT
    @Path("/{id}")
    public Response updateUser(@PathParam("id") Long id, User user) {
        try {
            adminService.updateUser(id, user);
            return Response.ok("User updated successfully!").build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    @DELETE
    @Path("/{id}")
    public Response deleteUser(@PathParam("id") Long id) {
        try {
            adminService.deleteUser(id);
            return Response.ok("User deleted successfully!").build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }
}
