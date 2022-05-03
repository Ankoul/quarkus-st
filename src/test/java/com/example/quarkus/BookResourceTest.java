package com.example.quarkus;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.notNullValue;

import com.example.quarkus.book.entity.Book;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.response.Response;
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
    public static final String VALID_TITLE = RandomStringUtils.randomAlphabetic(30);
    public static final String VALID_AUTHOR = "Gilson";
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
        final Book request = newBookInstance();

        Book createdBook = createBook(request)
                .then()
                .statusCode(200)
                .extract().body().jsonPath().getObject(".", Book.class);

        Assertions.assertEquals(request.getTitle(), createdBook.getTitle());
        Assertions.assertEquals(request.getAuthor(), createdBook.getAuthor());

        Book foundBook = getBookById(createdBook.getId());

        Assertions.assertEquals(request.getTitle(), foundBook.getTitle());
        Assertions.assertEquals(request.getAuthor(), foundBook.getAuthor());
    }

    private Book getBookById(final Long bookId) {
        return given()
                .when().get(BOOKS_PATH + "/" + bookId)
                .then()
                .statusCode(200)
                .extract().body().jsonPath().getObject(".", Book.class);
    }

    @Test
    @Order(3)
    public void listBooksShouldNotBeEmpty() {
        final List<Book> books = listBooks();

        Assertions.assertTrue(books.stream()
                .anyMatch(it -> it.getTitle().equals(VALID_TITLE) && it.getAuthor().equals(VALID_AUTHOR)));
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
        final Book request = newBookInstance();
        request.setAuthor(VALID_AUTHOR.toLowerCase());
        createBook(request).then().statusCode(400);

        final String invalidName = "any invalid name";
        request.setAuthor(invalidName);
        createBook(request).then().statusCode(400);

        final List<Book> books = listBooks();

        Assertions.assertTrue(books.stream().noneMatch(it -> it.getAuthor().equals(VALID_AUTHOR.toLowerCase())));
        Assertions.assertTrue(books.stream().noneMatch(it -> it.getAuthor().equals(invalidName)));
    }

    @Test
    @Order(6)
    public void titleShouldNotHaveMoreThan30Characters() throws JsonProcessingException {
        assertThatOnlyValidTitleIsAccepted(RandomStringUtils.randomAlphabetic(31));
    }

    @Test
    @Order(7)
    public void titleShouldNotBeNull() throws JsonProcessingException {
        assertThatOnlyValidTitleIsAccepted(null);
    }

    @Test
    @Order(8)
    public void titleShouldNotBeEmpty() throws JsonProcessingException {
        assertThatOnlyValidTitleIsAccepted("");
    }

    @Test
    @Order(9)
    public void titleShouldNotBeBlank() throws JsonProcessingException {
        assertThatOnlyValidTitleIsAccepted("    ");
    }

    private void assertThatOnlyValidTitleIsAccepted(final String invalidTitle) throws JsonProcessingException {
        final Book request = newBookInstance();
        request.setTitle(invalidTitle);
        createBook(request).then().statusCode(400);

        List<Book> books = listBooks();

        Assertions.assertTrue(books.stream().noneMatch(it -> it.getTitle().equals(invalidTitle)));

        request.setTitle(VALID_TITLE);
        createBook(request);
        final Book book = books.stream().findAny().orElseThrow();
        book.setTitle(invalidTitle);

        updateBook(book).then().statusCode(400);

        books = listBooks();

        Assertions.assertTrue(books.stream().noneMatch(it -> it.getTitle().equals(invalidTitle)));
    }

    private Book newBookInstance() {
        final Book request = new Book();
        request.setTitle(VALID_TITLE);
        request.setAuthor(VALID_AUTHOR);
        return request;
    }

    private List<Book> listBooks() {
        return given()
                .when().get(BOOKS_PATH)
                .then()
                .statusCode(200)
                .extract().body().jsonPath().getList(".", Book.class);
    }

    private Response updateBook(final Book book) throws JsonProcessingException {
        return given()
                .when()
                .header("Content-Type", "application/json")
                .body(objectMapper.writeValueAsString(book))
                .put(BOOKS_PATH + "/" + book.getId());
    }

    private Response createBook(final Book request) throws JsonProcessingException {
        return given()
                .when()
                .header("Content-Type", "application/json")
                .body(objectMapper.writeValueAsString(request))
                .post(BOOKS_PATH);
    }
}