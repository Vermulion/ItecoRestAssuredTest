import java.util.Random;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.oneOf;
import static org.junit.jupiter.api.Assertions.*;

import org.example.Endpoints;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;

public class SwaggerOrderTest {
    int invalidId = -5; // невалидный id
    int defunctId = 2707; // несущестующий id

    @BeforeAll
    public static void setUp() {
        RestAssured.requestSpecification = new RequestSpecBuilder()
                .setBaseUri(Endpoints.host + Endpoints.orderBasePath)
                .setAccept(ContentType.JSON)
                .setContentType(ContentType.JSON)
                .log(LogDetail.ALL)
                .build();
    }

    //генерация рандомных значений в  некоторых полях (PetId, Id, Quantity)
    private Order randomOrder() {
        Order order = new Order();
        order.setPetId(new Random().nextInt(10000));
        order.setId(new Random().nextInt(100));
        order.setQuantity(new Random().nextInt(10));
        return order;
    }

    private void createOrder(Order order) {
        given()
                .body(order)
                .when()
                .post(Endpoints.order)
                .then()
                .statusCode(200);
    }

    private Order getOrder(int id) {
        return given().pathParam("id", id)
                .when().get(Endpoints.order + Endpoints.orderId)
                .then().assertThat()
//                .statusCode(200)
                .extract().body().as(Order.class);
    }

    @Test
    @DisplayName("Тест создания и чтения новой записи")
    public void createOrderPositiveTest() {
        Order order = randomOrder();
        createOrder(order);
        Order apiOrder = getOrder(order.getId());
        assertEquals(order, apiOrder);
    }

    @Test
    @DisplayName("Тест создания новой записи с невалидным id")
    public void createOrderInvalidIdTest() {
        Order order = randomOrder();
        order.setId(invalidId);
        createOrder(order);
        Order apiOrder = getOrder(order.getId());
        // невалидный id при любом get-запросе вернет всегда 0
        assertTrue(apiOrder.getId() == 0);
    }

    @Test
    @DisplayName("Тест запроса записи с несуществующим id")
    public void getDefunctIdTest() {
        given().pathParam("id", defunctId)
                .log().body().
                when().get(Endpoints.order + Endpoints.orderId).
                then().log().body().assertThat().body("message", oneOf("Order not found"));
    }
}
