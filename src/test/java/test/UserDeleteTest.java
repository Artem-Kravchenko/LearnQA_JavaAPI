package test;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import lib.ApiCoreRequests;
import lib.Assertions;
import lib.BaseTestCase;
import lib.DataGenerator;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

public class UserDeleteTest extends BaseTestCase {

    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();

    @Test // Попытка удалить пользователя по ID 2 (Защищённого от удаления)
    public void deleteUserWithIdNumberTwoTest() {
        Map<String, String> authData = new HashMap<>();
        authData.put("email", "vinkotov@example.com");
        authData.put("password", "1234");

        Response responseLogin = apiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/api/user/login",
                        authData); //Логинимся под пользователем с ID = 2

        // Проверяем, что мы действительно залогинились под нужным пользователем
        Assertions.assertResponseCodeEquals(responseLogin, 200);
        Assertions.assertJsonByName(responseLogin, "user_id", 2);

        // Пробуем удалить этого пользователя
        Response responseDeleteUser = apiCoreRequests.makeDeleteRequestWithTokenAndCookie(
                "https://playground.learnqa.ru/api/user/",
                this.getHeader(responseLogin, "x-csrf-token"),
                this.getCookie(responseLogin, "auth_sid"));

        // Проверяем, что мы не можем удалить пользователя с ID = 2
        Assertions.assertResponseCodeEquals(responseDeleteUser, 400);
        Assertions.assertResponseTextEquals(responseDeleteUser, "Please, do not delete test users with ID 1, 2, 3, 4 or 5.");
    }

    @Test // Удаление только что созданного пользователя
    public void deleteJustCreatedUserTest() {
        // Создаём нового пользователя
        Map<String, String> userData = DataGenerator.getRegistrationData();

        JsonPath responseCreate = apiCoreRequests.makePostRequestToCreateNewUser(
                "https://playground.learnqa.ru/api/user/",
                userData).jsonPath();

        String userId = responseCreate.getString("id"); // Считываем его id

        // Логинимся только что созданным пользователем (И запрашиваем его данные)
        Map<String, String> authData = new HashMap<>();
        authData.put("email", userData.get("email"));
        authData.put("password", userData.get("password"));
        String[] expectedFields = {"username", "firstName", "lastName", "email"};

        Response responseLogin = apiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/api/user/login",
                        authData);

        Response responseUserInfo = apiCoreRequests.makeGetRequest(
                "https://playground.learnqa.ru/api/user/" + userId,
                this.getHeader(responseLogin, "x-csrf-token"),
                this.getCookie(responseLogin, "auth_sid"));

        Assertions.assertResponseCodeEquals(responseLogin, 200); // Проверяем, что успешно залогинились
        Assertions.assertJsonHasFields(responseUserInfo, expectedFields); // Дополнительно проверяем успешный логин

        // Удаляем только что созданного пользователя
        Response responseDeleteUser = apiCoreRequests.makeDeleteRequestWithTokenAndCookie(
                "https://playground.learnqa.ru/api/user/" + userId,
                this.getHeader(responseLogin, "x-csrf-token"),
                this.getCookie(responseLogin, "auth_sid"));

        // Пробуем получить данные удалённого пользователя по ID
        Response responseDeletedUserInfo = apiCoreRequests.makeGetRequest(
                "https://playground.learnqa.ru/api/user/" + userId,
                this.getHeader(responseLogin, "x-csrf-token"),
                this.getCookie(responseLogin, "auth_sid")
        );

        Assertions.assertResponseCodeEquals(responseLogin, 200);
        Assertions.assertResponseTextEquals(responseDeletedUserInfo, "User not found"); //Убеждаемся, что пользователь был действительно удалён
    }

    @Test
    public void deleteUserIfWeAreAuthorisedByOtherUserTest() {
        // Создаём нового пользователя
        Map<String, String> userData = DataGenerator.getRegistrationData();

        JsonPath responseCreate = apiCoreRequests.makePostRequestToCreateNewUser(
                "https://playground.learnqa.ru/api/user/",
                userData).jsonPath();

        String userId = responseCreate.getString("id"); // Считываем его id

        // Логинимся под другим пользователем
        Map<String, String> authData = new HashMap<>();
        authData.put("email", "vinkotov@example.com");
        authData.put("password", "1234");

        Response responseLogin = apiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/api/user/login",
                        authData); //Логинимся под пользователем с ID = 2

        // Проверяем, что мы действительно залогинились под нужным пользователем
        Assertions.assertResponseCodeEquals(responseLogin, 200);
        Assertions.assertJsonByName(responseLogin, "user_id", 2);

        // Пробуем удалить ранее созданного пользователя, будучи авторизованным под пользователем с ID = 2
        Response responseDelete = apiCoreRequests.makeDeleteRequestWithTokenAndCookie(
                "https://playground.learnqa.ru/api/user/" + userId,
                this.getHeader(responseLogin, "x-csrf-token"),
                this.getCookie(responseLogin, "auth_sid")
        );

        // Проверяем, что нельзя удалить одного пользователя, будучи авторизованным под другим
        Assertions.assertResponseCodeEquals(responseDelete, 400);
        Assertions.assertResponseTextEquals(responseDelete, "Please, do not delete test users with ID 1, 2, 3, 4 or 5.");
    }

}
