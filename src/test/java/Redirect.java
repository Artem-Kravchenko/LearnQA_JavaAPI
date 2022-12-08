import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

public class Redirect {
    @Test
    public void testRestAssured() {

        Response response = RestAssured
                .given()
                .redirects()
                .follow(false)
                .get("https://playground.learnqa.ru/api/long_redirect")
                .andReturn();

     //   int statusCode = response.getStatusCode();
     //  System.out.println(statusCode);

        String location = response.getHeader("Location"); // Извлекаем содержимое поля Location
        System.out.println(location); // Выводим содержимое поля Location
    }
}
