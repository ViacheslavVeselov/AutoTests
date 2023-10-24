package org.example.lab3;

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

public class LabThree {
    private static final String baseUrl =
            "https://a029cbc9-c01f-4bf9-9f8f-ddb8e12eeee9.mock.pstmn.io"; // базовий шлях

    private static final String OWNER_NAME = "/ownerName",
                                SUCCESS = "/success",
                                UNSUCCESS = "/unsuccess",
                                CREATE_SOMETHING = "/createSomething",
                                UPDATE_ME = "/updateMe",
                                DELETE_WORLD = "/deleteWorld";

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

    // Метод, що перевіряє успішне отримання імені
    @Test
    public void verifyGetOwnerNameSuccessAction() {
        // Збереження відповіді
        Response response = given()
                .get(OWNER_NAME + SUCCESS);

        // Перевірка статусу відповіді
        response.then().statusCode(HttpStatus.SC_OK);

        // Перевірка значення name у відповіді
        response.then().body("name", equalTo("Viacheslav Veselov"));
    }

    // Метод, що перевіряє не успішне отримання імені
    @Test
    public void verifyGetOwnerNameUnSuccessAction() {
        // Збереження відповіді
        Response response = given()
                .get(OWNER_NAME + UNSUCCESS);

        // Перевірка статусу відповіді
        response.then().statusCode(HttpStatus.SC_FORBIDDEN);

        // Перевірка значення name у відповіді
        response.then().body("exception", equalTo("I won't say my name!"));
    }

    // Метод, що перевіряє успішне створення
    @Test
    public void verifyCreateSomethingSuccessAction() {
        // Збереження відповіді
        Response response = given()
                .queryParam("permission", "yes")
                .post(CREATE_SOMETHING);

        // Перевірка статусу відповіді
        response.then().statusCode(HttpStatus.SC_OK);

        // Перевірка значення result у відповіді
        response.then().body("result", equalTo("'Nothing' was created"));
    }

    // Метод, що перевіряє не успішне створення (без параметру permission)
    @Test
    public void verifyCreateSomethingBadRequestAction() {
        // Збереження відповіді
        Response response = given()
                .post(CREATE_SOMETHING);

        // Перевірка статусу відповіді
        response.then().statusCode(HttpStatus.SC_BAD_REQUEST);

        // Перевірка значення result у відповіді
        response.then().body("result", equalTo("You don't have permission to create something"));
    }

    // Метод, що перевіряє оновлення
    @Test
    public void verifyUpdateMeAction() {
        // Створення тіла для запиту
        Map<String, ?> body = Map.of(
                "name", "",
                "surname", ""
        );

        // Перевірка статусу відповіді
        given().body(body)
                .put(UPDATE_ME)
                .then()
                .statusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR);
    }

    // Метод, що перевіряє видалення
    @Test
    public void verifyDeleteWorldAction() {
        // Збереження відповіді
        Response response = given()
                .header("SessionID", "123456789")
                .delete(DELETE_WORLD);

        // Перевірка статусу відповіді
        response.then().statusCode(HttpStatus.SC_GONE);

        // Перевірка значення world у відповіді
        response.then().body("world", equalTo("0"));
    }
}
