package test;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class TestCookie {
    @Test
    public void testCookie() {
        String cookieName;
        String cookieValue;

        Response respose = RestAssured
                .given()
                .get("https://playground.learnqa.ru/api/homework_cookie")
                .andReturn(); // Делаем запрос на API

        Map<String, String> cookies = respose.getCookies(); // Считываем все cookies в массив
        System.out.println(cookies); // Выводим список cookies на экран

        cookieName = cookies.keySet().toString()
                .replace("[","").replace("]", ""); //Считываем название cookie
        cookieValue = cookies.get("HomeWork"); //Считываем значение для cookie "HomeWork"

        assertEquals("HomeWork", cookieName, "Unexpected cookie name" + cookieName); //// Проверяем, что cookie называется "HomeWork"
        assertEquals("hw_value", cookieValue, "Unexpected cookie value " + cookieValue); // Проверяем, что "HomeWork" содержит значение hw_value

    }

}
