package test;

import io.restassured.RestAssured;
import io.restassured.http.Headers;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestHeader {
    @Test
    public void testHeader() {

        Response respose = RestAssured
                .given()
                .get("https://playground.learnqa.ru/api/homework_header")
                .andReturn(); // Делаем запрос на API

        Headers headers = respose.getHeaders(); // Получаем список всех заголовков
        headers.hasHeaderWithName("x-secret-homework-header"); // Проверяем, что заголовок с именем x-secret-homework-header действительно существует

        String header = headers.getValue("x-secret-homework-header"); // Считываем значение из заголовка x-secret-homework-header
        assertEquals("Some secret value", header, "Unexpected header value " + header); // Проверяем, что заголовок содержит значение "Some secret value"

    }
}
