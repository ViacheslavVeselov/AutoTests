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

public class TaskUserTest {
    private static final String baseUrl = "https://restful-booker.herokuapp.com"; // базовий шлях

    private static final String AUTH = "/auth",
            BOOKING = "/booking",
            BOOKING_ID = BOOKING + "/{id}";

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
    public void verifyAuthAction() {
        Map<String, ?> body = Map.of(
                "username", "admin",
                "password", "password123"
        );

        // Здійснення запиту та збереження відповіді
        Response response = given().body(body).post(AUTH);
        response.then().statusCode(HttpStatus.SC_OK);

        // Перевірка, що токен авторизації правильний
        String authToken = response.jsonPath().get("token").toString();
        RestAssured.requestSpecification.cookie("token", authToken);
    }

    // Метод, що перевіряє отримання id
    @Test
    public void verifyGetBookingIdsAction() {
        given().get(BOOKING)
                .then()
                .statusCode(HttpStatus.SC_OK);
    }

    // Метод, що перевіряє можливість створення нового замовлення
    @Test
    public void verifyCreateBookingAction() {
        // Створення тіла для запиту
        Map<String, ?> body = Map.of(
                "firstname", "Viacheslav",
                "lastname", "Veselov",
                "totalprice", Integer.valueOf("47"),
                "depositpaid", Boolean.valueOf("true"),
                "bookingdates", Map.of(
                        "checkin", Faker.instance().backToTheFuture().date(),
                        "checkout", Faker.instance().backToTheFuture().date()
                ),
                "additionalneeds", "Breakfast"
        );

        given().body(body)
                .post(BOOKING)
                .then()
                .statusCode(HttpStatus.SC_OK);
    }

    // Метод, що перевіряє можливість оновлення замовлення
    @Test(dependsOnMethods = "verifyAuthAction")
    public void verifyUpdateBookingAction() {
        // Створення тіла для запиту
        Map<String, ?> body = Map.of(
                "firstname", "Viacheslav",
                "lastname", "Veselov",
                "totalprice", Integer.valueOf(59),
                "depositpaid", Boolean.valueOf("true"),
                "bookingdates", Map.of(
                        "checkin", Faker.instance().backToTheFuture().date(),
                        "checkout", Faker.instance().backToTheFuture().date()
                ),
                "additionalneeds", "Breakfast"
        );

        Response response = given().body(body)
                .pathParam("id", 1)
                .put(BOOKING_ID);

        response.then().statusCode(HttpStatus.SC_OK);

        // Перевірка totalprice у відповіді
        response.then().body("totalprice", equalTo(59));
    }

    // Метод, що перевіряє отримання замовлення за його id
    @Test(dependsOnMethods = "verifyUpdateBookingAction")
    public void verifyGetBookingAction() {
        given().pathParam("id", 1)
                .get(BOOKING_ID)
                .then()
                .statusCode(HttpStatus.SC_OK)
                .and()
                .body("firstname", equalTo("Viacheslav"));
    }

    // Метод, що перевіряє можливість видалення створеного замовлення
    @Test(dependsOnMethods = "verifyGetBookingAction")
    public void verifyDeleteBookingAction() {
        given().pathParam("id", 1)
                .delete(BOOKING_ID)
                .then()
                .statusCode(HttpStatus.SC_CREATED);
    }
}
