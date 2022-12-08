import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import org.junit.jupiter.api.Test;

public class Tokens {
    @Test
    public void testRestAssured() throws InterruptedException {

        JsonPath response = RestAssured
                .given()
                .get("https://playground.learnqa.ru/ajax/api/longtime_job")
                .jsonPath(); // Запрос, который создаёт задачу

        String token = response.getString("token"); //Извлечение токена
        int seconds = response.getInt("seconds"); //Извлечение количества секунд, через которое задача будет выполнена

        Thread.sleep(seconds*1000 - 10000); //
                 response = RestAssured
                .given()
                .queryParam("token",token)
                .get("https://playground.learnqa.ru/ajax/api/longtime_job")
                .jsonPath(); //  Делаем запрос с token ДО того, как задача готова

        String status = response.getString("status");
        System.out.println(status); // Убеждаемся в правильности поля status

        Thread.sleep(10000);
                 response = RestAssured
                .given()
                .queryParam("token",token)
                .get("https://playground.learnqa.ru/ajax/api/longtime_job")
                .jsonPath(); //  Делаем запрос с token ПОСЛЕ того, как задача готова
        status = response.getString("status");
        String result = response.getString("result");
        System.out.println(status + " " + result); // Убеждаемся в правильности поля status и наличии поля result
    }

}

