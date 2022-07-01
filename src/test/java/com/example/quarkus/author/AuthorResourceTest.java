package com.example.quarkus.author;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.notNullValue;

import com.example.quarkus.author.boundary.AuthorService;
import com.example.quarkus.author.entity.Author;
import com.example.quarkus.book.entity.Book;
import io.quarkus.test.junit.QuarkusTest;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

@QuarkusTest
public class AuthorResourceTest {

    public static final String AUTHORS_PATH = "/authors";

    @Inject
    @RestClient
    AuthorService authorService;

    @Test
    void authorsListShouldNeverBeNull() {
        final List<Author> authors = listAllAuthors();
        Assertions.assertNotNull(authors);
    }

    @Test
    void authorsListShouldMatchExternalService() {
        final List<Author> expectedAuthors = authorService.listAuthors();
        final List<Author> returnedAuthors = listAllAuthors();

        final boolean allMatch = expectedAuthors.stream()
                .allMatch(author -> returnedAuthors.stream().anyMatch(it -> it.equals(author)));

        Assertions.assertTrue(allMatch);
        Assertions.assertEquals(expectedAuthors.size(), returnedAuthors.size());
    }

    private List<Author> listAllAuthors() {
        return given()
                .when().get(AUTHORS_PATH)
                .then()
                .statusCode(200)
                .extract().body().jsonPath().getList(".", Author.class);
    }

}
