package test;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import lib.ApiCoreRequests;
import lib.Assertions;
import lib.BaseTestCase;
import lib.DataGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.HashMap;
import java.util.Map;

public class UserRegisterTest extends BaseTestCase {
    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();

    @Test
    public void testCreateUserWithExistingEmail() {
        String email = "vinkotov@example.com";

        Map<String, String> userData = new HashMap<>();
        userData.put("email", email);
        userData = DataGenerator.getRegistrationData(userData);

        Response responseCreateAuth = RestAssured
                .given()
                .body(userData)
                .post("https://playground.learnqa.ru/api/user/")
                .andReturn();

        Assertions.assertResponseCodeEquals(responseCreateAuth, 400);
        Assertions.assertResponseTextEquals(responseCreateAuth, "Users with email '" + email + "' already exists");
    }

    @Test
    public void testCreateUserSuccessfully() {
        String email = DataGenerator.getRandomEmail();

        Map<String, String> userData = DataGenerator.getRegistrationData();

        Response responseCreateAuth = RestAssured
                .given()
                .body(userData)
                .post("https://playground.learnqa.ru/api/user/")
                .andReturn();

        Assertions.assertResponseCodeEquals(responseCreateAuth, 200);
        Assertions.assertJsonHasField(responseCreateAuth, "id");
    }

    @Test // Создание пользователя с некорректным email - без символа @
    public void testCreateUserWithoutAtSymbol() {
        String wrongEmail = "vinkotovexample.com";

        Map<String, String> userData = new HashMap<>();
        userData.put("email", wrongEmail);
        userData = DataGenerator.getRegistrationData(userData);

        Response responseCreateUserWithWrongEmail = apiCoreRequests
                .makePostRequestToCreateNewUser(
                        "https://playground.learnqa.ru/api/user/",
                        userData);

        Assertions.assertResponseCodeEquals(responseCreateUserWithWrongEmail, 400);
        Assertions.assertResponseTextEquals(responseCreateUserWithWrongEmail, "Invalid email format");
    }

    @ParameterizedTest // Создание пользователя без указания одного из полей
    @ValueSource(strings = {"email", "password", "username", "firstName", "lastName"})
    public void testCreateUserWithoutNecessaryField(String field) {
        Map<String, String> userData = new HashMap<>();
        userData.put(field, "");
        userData = DataGenerator.getRegistrationData(userData);

        Response responseCreateUserWithoutNecessaryField = apiCoreRequests
                .makePostRequestToCreateNewUser(
                        "https://playground.learnqa.ru/api/user/",
                        userData);

        Assertions.assertResponseCodeEquals(responseCreateUserWithoutNecessaryField, 400);
        Assertions.assertResponseTextEquals(responseCreateUserWithoutNecessaryField, "The value of '" + field + "' field is too short");
    }

    @ParameterizedTest // Создание пользователя с очень коротким именем в один символ (Поля username и firstName)
    @ValueSource(strings = {"username", "firstName"})
    public void testCreateUserWithNameInOneSymbol(String field) {
        Map<String, String> userData = new HashMap<>();
        userData.put(field, "A");
        userData = DataGenerator.getRegistrationData(userData);

        Response responseCreateUserWithNameInOneSymbol = apiCoreRequests
                .makePostRequestToCreateNewUser(
                        "https://playground.learnqa.ru/api/user/",
                        userData);

        Assertions.assertResponseCodeEquals(responseCreateUserWithNameInOneSymbol, 400);
        Assertions.assertResponseTextEquals(responseCreateUserWithNameInOneSymbol, "The value of '" + field + "' field is too short");
    }

    @ParameterizedTest // Создание пользователя с очень длинным именем - длиннее 250 символов (Поля username и firstName)
    @ValueSource(strings = {"username", "firstName"})
    public void testCreateUserWithLongName(String field) {
        Map<String, String> userData = new HashMap<>();
        userData.put(field, "12345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901");
        userData = DataGenerator.getRegistrationData(userData);

        Response responseCreateUserWithLongName = apiCoreRequests
                .makePostRequestToCreateNewUser(
                        "https://playground.learnqa.ru/api/user/",
                        userData);

        Assertions.assertResponseCodeEquals(responseCreateUserWithLongName, 400);
        Assertions.assertResponseTextEquals(responseCreateUserWithLongName, "The value of '" + field + "' field is too long");
    }


}



