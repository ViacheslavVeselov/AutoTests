package org.example.lab2;

import com.github.javafaker.Faker;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.parsing.Parser;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class UserTest {
    private static final String baseUrl = "https://petstore.swagger.io/v2"; // базовий шлях

    private static final String USER = "/user",
                                USER_USERNAME = USER + "/{username}",
                                USER_LOGIN = USER + "/login",
                                USER_LOGOUT = USER + "/logout";

    private String userName;
    private String firstName;

    // Метод, що виконується на початку всіх тестів
    @BeforeClass
    public void setup() {
        RestAssured.baseURI = baseUrl; // встановлення базового шляху
        RestAssured.defaultParser = Parser.JSON; // встановлення парсера за замовчуванням
        RestAssured.requestSpecification = new RequestSpecBuilder()
                .setContentType(ContentType.JSON) // тип даних тіла, яке передається в запиті
                .build();
        RestAssured.responseSpecification = new ResponseSpecBuilder().build();
    }

    // Метод для тестування можливості аутентифікації
    @Test
    public void verifyLoginAction() {
        Map<String, ?> body = Map.of(
                "username", "ViacheslavVeselov",
                "password", "122m-23-2.6"
        );

        // Здійснення запиту та збереження відповіді
        Response response = given().body(body).get(USER_LOGIN);
        response.then().statusCode(HttpStatus.SC_OK);

        RestAssured.requestSpecification.sessionId(
                response.jsonPath()
                        .get("message")
                        .toString()
                        .replaceAll("[^0-9]", "")
        );
    }

    // Метод, що перевіряє можливість створення нового користувача в системі
    @Test(dependsOnMethods = "verifyLoginAction")
    public void verifyCreateAction() {
        // Збереження імен
        userName = Faker.instance().name().username();
        firstName = Faker.instance().harryPotter().character();

        // Створення тіла для запиту
        Map<String, ?> body = Map.of(
                "username", userName,
                "firstName", firstName,
                "lastName", Faker.instance().gameOfThrones().character(),
                "email", Faker.instance().internet().emailAddress(),
                "password", Faker.instance().internet().password(),
                "phone", Faker.instance().phoneNumber().phoneNumber(),
                "userStatus", Integer.valueOf("1")
        );

        given().body(body)
                .post(USER)
                .then()
                .statusCode(HttpStatus.SC_OK);
    }

    // Метод, що перевіряє отримання даних про користувача
    @Test(dependsOnMethods = "verifyCreateAction")
    public void verifyGetAction() {
        given().pathParam("username", userName)
                .get(USER_USERNAME)
                .then()
                .statusCode(HttpStatus.SC_OK)
                .and()
                .body("firstName", equalTo(firstName));
    }

    // Метод, що перевіряє можливість видалення створеного користувача
    @Test(dependsOnMethods = "verifyGetAction")
    public void verifyDeleteAction() {
        given().pathParam("username", userName)
                .delete(USER_USERNAME)
                .then()
                .statusCode(HttpStatus.SC_OK);
    }

    // Метод, що перевіряє можливість виходу з акаунта
    @Test(dependsOnMethods = "verifyLoginAction", priority = 1)
    public void verifyLogoutAction() {
        given().get(USER_LOGOUT)
                .then()
                .statusCode(HttpStatus.SC_OK);
    }
}
