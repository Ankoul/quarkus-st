package com.example.quarkus.author.boundary;

import com.example.quarkus.author.entity.Author;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@RegisterRestClient
@Path("/authors")
public interface AuthorService {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    List<Author> listAuthors();

}
