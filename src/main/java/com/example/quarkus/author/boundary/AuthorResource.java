package com.example.quarkus.author.boundary;

import com.example.quarkus.author.control.AuthorController;
import com.example.quarkus.author.entity.Author;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/authors")
public class AuthorResource {

    @Inject
    AuthorController authorController;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Author> findAll() {
        return authorController.findAll();
    }

}