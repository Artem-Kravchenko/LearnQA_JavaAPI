package lib;

import io.restassured.response.Response;

import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class Assertions {
    public static void assertJsonByName(Response Response, String name, int expectedValue) {
        Response.then().assertThat().body("$", hasKey(name));

        int value = Response.jsonPath().getInt(name);
        assertEquals(expectedValue, value, "JSON value is not equal to expected value");
    }

    public static void assertJsonByName(Response Response, String name, String expectedValue) {
        Response.then().assertThat().body("$", hasKey(name));

        String value = Response.jsonPath().getString(name);
        assertEquals(expectedValue, value, "JSON value is not equal to expected value");
    }

    public static void assertResponseTextEquals(Response response, String expectedAnswer) {
        assertEquals(
                expectedAnswer,
                response.asString(),
                "Response text is not as expected"
        );
    }

    public static void assertResponseCodeEquals(Response response, int expectedStatusCode) {
        assertEquals(
                expectedStatusCode,
                response.statusCode(),
                "Response code is not as expected"
        );
    }

    public static void assertJsonHasFields(Response response, String[] expectedFieldNames) {
        for (String expectedFieldName : expectedFieldNames){
            Assertions.assertJsonHasField(response, expectedFieldName);
        }
    }

    public static void assertJsonHasField(Response response, String expectedFieldName) {
        response.then().assertThat().body("$", hasKey(expectedFieldName));
    }

    public static void assertJsonHasNotField(Response response, String unexpectedFieldName) {
        response.then().assertThat().body("$", not(hasKey(unexpectedFieldName)));
    }
}

