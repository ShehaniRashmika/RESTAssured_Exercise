package API_Tests;


import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

import org.json.JSONObject;

public class GetUserDetails {
	private static final String BASE_URL = "https://mzo5slmo45.execute-api.eu-west-2.amazonaws.com/v1";
	private static final String API_KEY = "GombImxOhMCa8AqMmNM9KEFwaSHSFHty";

	@BeforeClass
       public void setup() {
        RestAssured.baseURI = "https://mzo5slmo45.execute-api.eu-west-2.amazonaws.com/v1";
        }
	
	@Test
	@Severity(SeverityLevel.CRITICAL)
	public void testGetUserDetails_ValidUserId() {

		given()
		.header("Authorization", "Bearer " + API_KEY)
		.when()
		.get("/users/75c902fa-0ef8-46e3-9145-e130699ea49e") //  a valid user ID
		.then()
		.statusCode(200)
		.body("status", equalTo("Success"))
		.body("data.userId", equalTo("75c902fa-0ef8-46e3-9145-e130699ea49e"))
		.body("data.status", equalTo("active"));
	}


	@Test
	@Severity(SeverityLevel.CRITICAL)
	public void testGetUserDetails_InValidUserId() {

		given()
		.header("Authorization", "Bearer " + API_KEY)
		.when()
		.get("/users/bcc1e22a-9b5f-4d51-a815-91e6fb6") //  an invalid user ID
		.then()
		.statusCode(400);
	}
	
	@Test
	@Severity(SeverityLevel.CRITICAL)
	public void testGetUserDetails_NoUserId() {

		given()
		.header("Authorization", "Bearer " + API_KEY)
		.when()
		.get("/users") //  an invalid user ID
		.then()
		.statusCode(403);
	}
	
	@Test
	@Severity(SeverityLevel.CRITICAL)
        public void testGetUserDetailsResponseFormat() {
        String userId = createUserAndGetUserId();

        given()
            .header("Authorization", API_KEY)
        .when()
            .get("/users/" + userId)
        .then()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .body("status", equalTo("Success"))
            .body("data.userId", equalTo(userId))
            .body("data.status", anyOf(equalTo("active"), equalTo("new"), equalTo("rejected")))
            .body("data.title", equalTo("Mr"))
            .body("data.firstName", equalTo("John"))
            .body("data.lastName", equalTo("Doe"))
            .body("data.dateOfBirth", equalTo("1987-06-04"))
            .body("data.email", equalTo("somefake@email.com"))
            .body("data.rating", equalTo(10))
            .body("error", nullValue());
    }

    private String createUserAndGetUserId() {
        JSONObject requestParams = new JSONObject();
        requestParams.put("title", "Mr");
        requestParams.put("firstName", "John");
        requestParams.put("lastName", "Doe");
        requestParams.put("dateOfBirth", "1987-06-04");
        requestParams.put("email", "somefake@email.com");
        requestParams.put("password", "super secret password");
        requestParams.put("rating", 10);

        Response response = given()
            .header("Authorization", API_KEY)
            .contentType(ContentType.JSON)
            .body(requestParams.toString())
        .when()
            .post("/users")
        .then()
            .extract().response();

        return response.jsonPath().getString("data.userId");
    }

}

