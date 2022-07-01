package com.example.quarkus.author.control;

import com.example.quarkus.author.boundary.AuthorService;
import com.example.quarkus.author.entity.Author;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
@SuppressWarnings("CdiInjectionPointsInspection")
public class AuthorController {

    @Inject
    @RestClient
    AuthorService authorService;

    public List<Author> findAll() {
        return authorService.listAuthors();
    }
}
