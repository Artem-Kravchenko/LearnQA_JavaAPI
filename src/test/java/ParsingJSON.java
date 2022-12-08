import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import org.junit.jupiter.api.Test;

public class ParsingJSON {
    @Test
    public void testRestAssured() {

        JsonPath response = RestAssured
                .given()
                .get("https://playground.learnqa.ru/api/get_json_homework")
                .jsonPath();

        String secondMessage = response.getString("messages[1]");
        System.out.println(secondMessage); // Вывод элемента массива с нужным сообщением

        System.out.println(secondMessage.replace("message:", "").replace("[", "")
                .replace("]", "")); //Вывод только нужного сообщения
    }

}
