package com.example.quarkus;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.notNullValue;

import com.example.quarkus.book.entity.Book;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.test.junit.QuarkusTest;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.util.List;

@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BookResourceTest {

    public static final String BOOKS_PATH = "/books";
    public static final String TITLE = "title test";
    public static final String AUTHOR = "Gilson";
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @Order(1)
    public void listBooksShouldNeverBeNull() {
        given()
                .when().get(BOOKS_PATH)
                .then()
                .statusCode(200)
                .body(notNullValue());
    }

    @Test
    @Order(2)
    public void booksShouldBeCreatedSuccessful() throws JsonProcessingException {
        final Book request = new Book();
        request.setTitle(TITLE);
        request.setAuthor(AUTHOR);

        Book createdBook = given()
                .when()
                .header("Content-Type", "application/json")
                .body(objectMapper.writeValueAsString(request))
                .post(BOOKS_PATH)
                .then()
                .statusCode(200)
                .extract().body().jsonPath().getObject(".", Book.class);

        Assertions.assertEquals(request.getTitle(), createdBook.getTitle());
        Assertions.assertEquals(request.getAuthor(), createdBook.getAuthor());

        Book foundBook = given()
                .when().get(BOOKS_PATH + "/" + createdBook.getId())
                .then()
                .statusCode(200)
                .extract().body().jsonPath().getObject(".", Book.class);

        Assertions.assertEquals(request.getTitle(), foundBook.getTitle());
        Assertions.assertEquals(request.getAuthor(), foundBook.getAuthor());
    }

    @Test
    @Order(3)
    public void listBooksShouldNotBeEmpty() {
        final List<Book> books = given()
                .when().get(BOOKS_PATH)
                .then()
                .statusCode(200)
                .extract().body().jsonPath().getList(".", Book.class);

        Assertions.assertTrue(books.stream().anyMatch(it -> it.getTitle().equals(TITLE) && it.getAuthor().equals(AUTHOR)));
    }

    @Test
    @Order(4)
    public void getShouldReturnNotFound() {
        given()
                .when().get(BOOKS_PATH + "/999999")
                .then()
                .statusCode(404);
    }

    @Test
    @Order(5)
    public void booksShouldThrowErrorForInvalidAuthor() throws JsonProcessingException {
        final Book request = new Book();
        request.setTitle(TITLE);
        request.setAuthor(AUTHOR.toLowerCase());

        given()
                .when()
                .header("Content-Type", "application/json")
                .body(objectMapper.writeValueAsString(request))
                .post(BOOKS_PATH)
                .then()
                .statusCode(400);

        final String invalidName = "any invalid name";
        request.setAuthor(invalidName);
        given()
                .when()
                .header("Content-Type", "application/json")
                .body(objectMapper.writeValueAsString(request))
                .post(BOOKS_PATH)
                .then()
                .statusCode(400);

        final List<Book> books = given()
                .when().get(BOOKS_PATH)
                .then()
                .statusCode(200)
                .extract().body().jsonPath().getList(".", Book.class);

        Assertions.assertTrue(books.stream().noneMatch(it -> it.getAuthor().equals(AUTHOR.toLowerCase())));
        Assertions.assertTrue(books.stream().noneMatch(it -> it.getAuthor().equals(invalidName)));
    }

    @Test
    @Order(6)
    public void titleShouldNotHaveMoreThan30Characters() throws JsonProcessingException {
        final Book request = new Book();
        request.setTitle(RandomStringUtils.randomAlphabetic(30));
        request.setAuthor(AUTHOR);

        given()
                .when()
                .header("Content-Type", "application/json")
                .body(objectMapper.writeValueAsString(request))
                .post(BOOKS_PATH)
                .then()
                .statusCode(200);

        final String title50CharLength = RandomStringUtils.randomAlphabetic(50);
        request.setTitle(title50CharLength);
        given()
                .when()
                .header("Content-Type", "application/json")
                .body(objectMapper.writeValueAsString(request))
                .post(BOOKS_PATH)
                .then()
                .statusCode(400);

        List<Book> books = given()
                .when().get(BOOKS_PATH)
                .then()
                .statusCode(200)
                .extract().body().jsonPath().getList(".", Book.class);

        Assertions.assertTrue(books.stream().noneMatch(it -> it.getTitle().equals(title50CharLength)));

        final Book book = books.stream().findAny().orElseThrow();
        book.setTitle(title50CharLength);
        given()
                .when()
                .header("Content-Type", "application/json")
                .body(objectMapper.writeValueAsString(book))
                .put(BOOKS_PATH + "/" + book.getId())
                .then()
                .statusCode(400);

        books = given()
                .when().get(BOOKS_PATH)
                .then()
                .statusCode(200)
                .extract().body().jsonPath().getList(".", Book.class);

        Assertions.assertTrue(books.stream().noneMatch(it -> it.getTitle().equals(title50CharLength)));
    }

    @Test
    @Order(7)
    public void titleShouldNotBeNull() throws JsonProcessingException {
        final Book request = new Book();
        request.setTitle(RandomStringUtils.randomAlphabetic(30));
        request.setAuthor(AUTHOR);

        given()
                .when()
                .header("Content-Type", "application/json")
                .body(objectMapper.writeValueAsString(request))
                .post(BOOKS_PATH)
                .then()
                .statusCode(200);

        request.setTitle(null);
        given()
                .when()
                .header("Content-Type", "application/json")
                .body(objectMapper.writeValueAsString(request))
                .post(BOOKS_PATH)
                .then()
                .statusCode(400);

        List<Book> books = given()
                .when().get(BOOKS_PATH)
                .then()
                .statusCode(200)
                .extract().body().jsonPath().getList(".", Book.class);

        Assertions.assertTrue(books.stream().noneMatch(it -> it.getTitle() == null));

        final Book book = books.stream().findAny().orElseThrow();
        book.setTitle(null);
        given()
                .when()
                .header("Content-Type", "application/json")
                .body(objectMapper.writeValueAsString(book))
                .put(BOOKS_PATH + "/" + book.getId())
                .then()
                .statusCode(400);

        books = given()
                .when().get(BOOKS_PATH)
                .then()
                .statusCode(200)
                .extract().body().jsonPath().getList(".", Book.class);

        Assertions.assertTrue(books.stream().noneMatch(it -> it.getTitle() == null));
    }

    @Test
    @Order(8)
    public void titleShouldNotBeEmpty() throws JsonProcessingException {
        final Book request = new Book();
        request.setTitle("test");
        request.setAuthor(AUTHOR);

        given()
                .when()
                .header("Content-Type", "application/json")
                .body(objectMapper.writeValueAsString(request))
                .post(BOOKS_PATH)
                .then()
                .statusCode(200);

        request.setTitle("");
        given()
                .when()
                .header("Content-Type", "application/json")
                .body(objectMapper.writeValueAsString(request))
                .post(BOOKS_PATH)
                .then()
                .statusCode(400);

        List<Book> books = given()
                .when().get(BOOKS_PATH)
                .then()
                .statusCode(200)
                .extract().body().jsonPath().getList(".", Book.class);

        Assertions.assertTrue(books.stream().noneMatch(it -> it.getTitle().equals("")));

        final Book book = books.stream().findAny().orElseThrow();
        book.setTitle("");
        given()
                .when()
                .header("Content-Type", "application/json")
                .body(objectMapper.writeValueAsString(book))
                .put(BOOKS_PATH + "/" + book.getId())
                .then()
                .statusCode(400);

        books = given()
                .when().get(BOOKS_PATH)
                .then()
                .statusCode(200)
                .extract().body().jsonPath().getList(".", Book.class);

        Assertions.assertTrue(books.stream().noneMatch(it -> it.getTitle().equals("")));
    }

    @Test
    @Order(9)
    public void titleShouldNotBeBlank() throws JsonProcessingException {
        final Book request = new Book();
        request.setTitle("test");
        request.setAuthor(AUTHOR);

        given()
                .when()
                .header("Content-Type", "application/json")
                .body(objectMapper.writeValueAsString(request))
                .post(BOOKS_PATH)
                .then()
                .statusCode(200);

        final String blank = "    ";
        request.setTitle(blank);
        given()
                .when()
                .header("Content-Type", "application/json")
                .body(objectMapper.writeValueAsString(request))
                .post(BOOKS_PATH)
                .then()
                .statusCode(400);

        List<Book> books = given()
                .when().get(BOOKS_PATH)
                .then()
                .statusCode(200)
                .extract().body().jsonPath().getList(".", Book.class);

        Assertions.assertTrue(books.stream().noneMatch(it -> it.getTitle().equals(blank)));

        final Book book = books.stream().findAny().orElseThrow();
        book.setTitle(blank);
        given()
                .when()
                .header("Content-Type", "application/json")
                .body(objectMapper.writeValueAsString(book))
                .put(BOOKS_PATH + "/" + book.getId())
                .then()
                .statusCode(400);

        books = given()
                .when().get(BOOKS_PATH)
                .then()
                .statusCode(200)
                .extract().body().jsonPath().getList(".", Book.class);

        Assertions.assertTrue(books.stream().noneMatch(it -> it.getTitle().equals(blank)));
    }
}