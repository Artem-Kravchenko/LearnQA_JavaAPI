package test;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserAgent {

    @ParameterizedTest
    @ValueSource(strings = {
            "Mozilla/5.0 (Linux; U; Android 4.0.2; en-us; Galaxy Nexus Build/ICL53F) AppleWebKit/534.30 (KHTML, like Gecko) Version/4.0 Mobile Safari/534.30",
            "Mozilla/5.0 (iPad; CPU OS 13_2 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) CriOS/91.0.4472.77 Mobile/15E148 Safari/604.1",
            "Mozilla/5.0 (compatible; Googlebot/2.1; +http://www.google.com/bot.html)",
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.77 Safari/537.36 Edg/91.0.100.0",
            "Mozilla/5.0 (iPad; CPU iPhone OS 13_2_3 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/13.0.3 Mobile/15E148 Safari/604.1"
    })
    public void testUserAgent(String userAgent) {
        Map<String, String> headers = new HashMap<>();
        headers.put("User-Agent", userAgent);

        JsonPath respose = RestAssured
                .given()
                .headers(headers)
                .get("https://playground.learnqa.ru/ajax/api/user_agent_check")
                .jsonPath(); // Делаем запрос на API

        String platform = respose.getString("platform"); // Считываем фактическое значение платформы
        String browser = respose.getString("browser"); // Считываем фактическое значение браузера
        String device = respose.getString("device"); // Считываем фактическое значение устройства

       //Идёт сравнение фактического значения с ожидаемым
        switch (userAgent) {
            case "Mozilla/5.0 (Linux; U; Android 4.0.2; en-us; Galaxy Nexus Build/ICL53F) AppleWebKit/534.30 (KHTML, like Gecko) Version/4.0 Mobile Safari/534.30":
                assertEquals("Mobile", platform, "Unexpected platform value " + platform);
                assertEquals("No", browser, "Unexpected browser value " + browser);
                assertEquals("Android", device, "Unexpected device value " + device);
                break;
            case "Mozilla/5.0 (iPad; CPU OS 13_2 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) CriOS/91.0.4472.77 Mobile/15E148 Safari/604.1":
                assertEquals("Mobile", platform, "Unexpected platform value " + platform);
                assertEquals("Chrome", browser, "Unexpected browser value " + browser);
                assertEquals("iOS", device, "Unexpected device value " + device);
                break;
            case "Mozilla/5.0 (compatible; Googlebot/2.1; +http://www.google.com/bot.html)":
                assertEquals("Googlebot", platform, "Unexpected platform value " + platform);
                assertEquals("Unknown", browser, "Unexpected browser value " + browser);
                assertEquals("Unknown", device, "Unexpected device value " + device);
                break;
            case "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.77 Safari/537.36 Edg/91.0.100.0":
                assertEquals("Web", platform, "Unexpected platform value " + platform);
                assertEquals("Chrome", browser, "Unexpected browser value " + browser);
                assertEquals("No", device, "Unexpected device value " + device);
                break;
            case "Mozilla/5.0 (iPad; CPU iPhone OS 13_2_3 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/13.0.3 Mobile/15E148 Safari/604.1":
                assertEquals("Mobile", platform, "Unexpected platform value " + platform);
                assertEquals("Web", browser, "Unexpected browser value " + browser);
                assertEquals("iPhone", device, "Unexpected device value " + device);
                break;
        }
    }

}
