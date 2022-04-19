package com.example.quarkus;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.notNullValue;

import com.example.quarkus.book.entity.Book;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.test.junit.QuarkusTest;
import net.minidev.json.JSONObject;
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

    private ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @Order(1)
    public void booksShouldBeCreatedSuccessful() throws JsonProcessingException {
        final Book request = new Book();
        request.setTitle("title test");
        request.setAuthor("Gilson");

        final Book response = given()
                .when()
                .header("Content-Type", "application/json")
                .body(objectMapper.writeValueAsString(request))
                .post(BOOKS_PATH)
                .then()
                .statusCode(200)
                .extract().body().jsonPath().getObject(".", Book.class);

        Assertions.assertEquals(request.getTitle(), response.getTitle());
        Assertions.assertEquals(request.getAuthor(), response.getAuthor());
    }

    @Test
    @Order(2)
    public void listBooksShouldNeverBeNull() {
        given()
                .when().get(BOOKS_PATH)
                .then()
                .statusCode(200)
                .body(notNullValue());
    }

}