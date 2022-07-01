package com.example.quarkus.author.boundary;

import com.example.quarkus.author.entity.Author;
import com.example.quarkus.book.entity.Book;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@RegisterRestClient
@Path("/authors")
public interface AuthorService {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    List<Author> listAuthors();

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}/books")
    List<Book> listBooksByAuthorId(@PathParam("id") Long id);

}
