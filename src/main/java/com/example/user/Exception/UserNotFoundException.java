package com.example.user.Exception;



import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

public class UserNotFoundException extends WebApplicationException {
    public UserNotFoundException(String message) {
        super(Response.status(Response.Status.NOT_FOUND).entity(message).build());
    }
}
