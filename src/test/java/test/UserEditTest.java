package test;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import lib.ApiCoreRequests;
import lib.Assertions;
import lib.BaseTestCase;
import lib.DataGenerator;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

public class UserEditTest extends BaseTestCase {

    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();

    @Test
    public void editJustCreatedTest() {
        // GENERATE USER
        Map<String, String> userData = DataGenerator.getRegistrationData();

        JsonPath responseCreateAuth = RestAssured
                .given()
                .body(userData)
                .post("https://playground.learnqa.ru/api/user/")
                .jsonPath();

        String userId = responseCreateAuth.getString("id");

        // LOGIN
        Map<String, String> authData = new HashMap<>();
        authData.put("email", userData.get("email"));
        authData.put("password", userData.get("password"));

        Response responseGetAuth = RestAssured
                .given()
                .body(authData)
                .post("https://playground.learnqa.ru/api/user/login")
                .andReturn();

        //EDIT
        String newName = "Changed Name";
        Map<String, String> editData = new HashMap<>();
        editData.put("firstName", newName);

        Response responseEditUser = RestAssured
                .given()
                .header("x-csrf-token", this.getHeader(responseGetAuth, "x-csrf-token"))
                .cookie("auth_sid", this.getCookie(responseGetAuth, "auth_sid"))
                .body(editData)
                .put("https://playground.learnqa.ru/api/user/" + userId)
                .andReturn();

        //GET
        Response responseUserData = RestAssured
                .given()
                .header("x-csrf-token", this.getHeader(responseGetAuth, "x-csrf-token"))
                .cookie("auth_sid", this.getCookie(responseGetAuth, "auth_sid"))
                .get("https://playground.learnqa.ru/api/user/" + userId)
                .andReturn();

        Assertions.assertJsonByName(responseUserData, "firstName", newName);
    }

    @Test // Попытка изменить данные пользователя, будучи неавторизованными
    public void editIfWeAreNotAuthorisedTest() {
        String id = "1";
        String newName = "Changed Name";
        Map<String, String> editData = new HashMap<>();
        editData.put("firstName", newName);

        Response responseEditUser = apiCoreRequests.makePutRequestWithoutTokenAndCookie(
                "https://playground.learnqa.ru/api/user/" + id,
                editData);

        Assertions.assertResponseCodeEquals(responseEditUser, 400);
        Assertions.assertResponseTextEquals(responseEditUser, "Auth token not supplied");
    }

    @Test // Попытка изменить данные пользователя, будучи авторизованным другим пользователем
    public void editIfWeAreAuthorisedByAnotherUserTest() {
        // Создаём нового пользователя
        Map<String, String> userData = DataGenerator.getRegistrationData();

        JsonPath responseCreateAuth = apiCoreRequests.makePostRequestToCreateNewUser(
                "https://playground.learnqa.ru/api/user/",
                userData).jsonPath();

        String userId = responseCreateAuth.getString("id"); // Считываем его id

        // Логинимся другим пользователем (Для удобства был взят пользователь с id = 2)
        Map<String, String> authData = new HashMap<>();
        authData.put("email", "vinkotov@example.com");
        authData.put("password", "1234");

        Response responseAuth = apiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/api/user/login",
                        authData);

        // Проверяем, что мы действительно залогинились под нужным пользователем
        Assertions.assertResponseCodeEquals(responseAuth, 200);
        Assertions.assertJsonByName(responseAuth, "user_id", 2);

        //Пытаемся изменить данные другого (Ранее созданного) пользователя
        String newName = "Changed Name";
        Map<String, String> editData = new HashMap<>();
        editData.put("firstName", newName);

        Response responseEditUser = apiCoreRequests.makePutRequestWithTokenAndCookie(
                "https://playground.learnqa.ru/api/user/" + userId,
                this.getHeader(responseAuth, "x-csrf-token"),
                this.getCookie(responseAuth, "auth_sid"),
                editData);

        // Проверяем, что мы не можем изменить данные ранее созданного пользователя, будучи авторизованными другим пользователем
        Assertions.assertResponseCodeEquals(responseEditUser, 400);
        Assertions.assertResponseTextEquals(responseEditUser, "Please, do not edit test users with ID 1, 2, 3, 4 or 5.");
    }

    @Test // Попытка изменить email пользователя, будучи авторизованными тем же пользователем, на новый email без символа @
    public void editEmailWeAreAuthorisedAsSameUserTest() {
        // Создаём нового пользователя
        Map<String, String> userData = DataGenerator.getRegistrationData();

        JsonPath responseCreate = apiCoreRequests.makePostRequestToCreateNewUser(
                "https://playground.learnqa.ru/api/user/",
                userData).jsonPath();

        String userId = responseCreate.getString("id"); // Считываем его id
        String email = userData.get("email"); // Считываем его email

        // Логинимся только что созданным пользователем
        Map<String, String> authData = new HashMap<>();
        authData.put("email", userData.get("email"));
        authData.put("password", userData.get("password"));

        Response responseLogin = apiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/api/user/login",
                        authData);

        Assertions.assertResponseCodeEquals(responseLogin, 200); // Проверяем, что успешно залогинились

        //Редактируем email на новый без символа @
        String newEmail = email.replace("@", "");
        Map<String, String> editData = new HashMap<>();
        editData.put("email", newEmail);

        Response responseEditUser = apiCoreRequests.makePutRequestWithTokenAndCookie(
                "https://playground.learnqa.ru/api/user/" + userId,
                this.getHeader(responseLogin, "x-csrf-token"),
                this.getCookie(responseLogin, "auth_sid"),
                editData);

        Assertions.assertResponseCodeEquals(responseLogin, 200);
        Assertions.assertResponseTextEquals(responseEditUser, "Invalid email format"); //Проверяем текст сообщения об ошибке
    }

    @Test //
    public void editFirstNameWeAreAuthorisedAsSameUserTest() {
        // Создаём нового пользователя
        Map<String, String> userData = DataGenerator.getRegistrationData();

        JsonPath responseCreate = apiCoreRequests.makePostRequestToCreateNewUser(
                "https://playground.learnqa.ru/api/user/",
                userData).jsonPath();

        String userId = responseCreate.getString("id"); // Считываем его id
        String firstName = userData.get("firstName"); // Считываем его firstName

        // Логинимся только что созданным пользователем
        Map<String, String> authData = new HashMap<>();
        authData.put("email", userData.get("email"));
        authData.put("password", userData.get("password"));

        Response responseLogin = apiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/api/user/login",
                        authData);

        Assertions.assertResponseCodeEquals(responseLogin, 200); // Проверяем, что успешно залогинились

        //Редактируем firstName на очень короткое значение в один символ
        String newFirstName = "A";
        Map<String, String> editData = new HashMap<>();
        editData.put("firstName", newFirstName);

        Response responseEditUser = apiCoreRequests.makePutRequestWithTokenAndCookie(
                "https://playground.learnqa.ru/api/user/" + userId,
                this.getHeader(responseLogin, "x-csrf-token"),
                this.getCookie(responseLogin, "auth_sid"),
                editData);

        Assertions.assertResponseCodeEquals(responseLogin, 200);
        Assertions.assertJsonByName(responseEditUser,
                "error",
                "Too short value for field firstName"); //Проверяем текст сообщения об ошибке
    }
}
