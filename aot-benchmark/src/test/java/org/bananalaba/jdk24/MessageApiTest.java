package org.bananalaba.jdk24;

import io.restassured.RestAssured;
import static io.restassured.RestAssured.delete;
import static io.restassured.RestAssured.get;
import static io.restassured.RestAssured.post;
import static org.hamcrest.Matchers.equalTo;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class MessageApiTest {

    @BeforeAll
    static void setUp() {
        RestAssured.baseURI = "http://localhost:8080";
        RestAssured.port = 8080;
    }

    @Test
    void shouldGetMessage() {
        post("api/v1/messages/1?text=test")
            .then()
            .statusCode(200);

        get("api/v1/messages/1")
            .then()
            .statusCode(200)
            .body("text", equalTo("test"));
    }

    @Test
    void shouldDeleteMessage() {
        post("api/v1/messages/2?text=test2")
            .then()
            .statusCode(200);

        delete("/api/v1/messages/2")
            .then()
            .statusCode(200);

        get("/api/v1/messages/2")
            .then()
            .statusCode(404);
    }

}
