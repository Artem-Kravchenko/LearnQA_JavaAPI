import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

public class LongRedirect {
    @Test
    public void testRestAssured() {

        Response response;

        String location = "https://playground.learnqa.ru/api/long_redirect"; // Задаём начальное значение URL
        int statusCode = 0; // Задаём дефолтное значение statusCode

        while (statusCode!=200)
            {
                response = RestAssured
                        .given()
                        .redirects()
                        .follow(false)
                        .get(location)
                        .andReturn();
                location = response.getHeader("Location");
                if (location != null) {
                    System.out.println(location); //Вывод списка URL'ов, на которые идёт редирект
                }
                statusCode = response.getStatusCode();
            }
        System.out.println(statusCode); //Вывод финального statusCode

    }
}
