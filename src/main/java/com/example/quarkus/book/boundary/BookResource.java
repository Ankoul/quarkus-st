package com.example.quarkus.book.boundary;

import com.example.quarkus.book.control.BookController;
import com.example.quarkus.book.entity.Book;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/books")
public class BookResource {

    @Inject
    BookController bookController;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Book> findAll() {
        return bookController.findAll();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Book findOne(@PathParam("id") Long id) {
        return bookController.findById(id);
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Book create(Book book) {
        return bookController.create(book);
    }

    @PUT
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Book update(@PathParam("id") Long id, Book book) {
        return bookController.update(id, book);
    }

    @DELETE
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public void delete(@PathParam("id") Long id) {
        bookController.deleteById(id);
    }

}