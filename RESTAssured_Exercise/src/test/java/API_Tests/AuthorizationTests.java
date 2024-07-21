package API_Tests;


import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class AuthorizationTests {

	private static final String BASE_URL = "https://mzo5slmo45.execute-api.eu-west-2.amazonaws.com/v1";
	private static final String API_KEY = "GombImxOhMCa8AqMmNM9KEFwaSHSFHty";
	private static final String VALID_USER_ID = "75c902fa-0ef8-46e3-9145-e130699ea49e";
	private static final String INVALID_API_KEY = "GombImxOhMCa";
	private static final String JSON_REQUEST_BODY = "{\n"
			+ "\"title\": \"Mr\",\n"
			+ "\"firstName\": \"Jane\",\n"
			+ "\"lastName\": \"Johns\",\n"
			+ "\"dateOfBirth\": \"1985-06-04\",\n"
			+ "\"email\": \"jane123@email.com\",\n"
			+ "\"password\": \"123456543erfgfx\",\n"
			+ "\"rating\": 8\n"
			+ "}";

	@BeforeClass
	public void setUp() {
		RestAssured.baseURI = BASE_URL;
	}

	// Helper method to create user
	private Response createUser(String requestBody, String apiKey) {
		try {
			return given()
					.header("Authorization", "Bearer " + apiKey)
					.contentType("application/json")
					.body(requestBody)
					.when()
					.post("/users")
					.then()
					.extract().response();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	// Helper method to get user details
	private Response getUser(String userId, String apiKey) {
		try {
			return given()
					.header("Authorization", "Bearer " + apiKey)
					.when()
					.get("/users/" + userId)
					.then()
					.extract().response();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Test
	@Severity(SeverityLevel.CRITICAL)
	public void testGETValidApiKey() {
		Response response = getUser(VALID_USER_ID, API_KEY);

		if (response != null) {
			response.then()
			.statusCode(200);
		} else {
			System.out.println("Failed to connect to the server.");
		}
	}

	@Test
	@Severity(SeverityLevel.CRITICAL)
	public void testGETMissingApiKey() {
		Response response = getUser(VALID_USER_ID, "");

		if (response != null) {
			response.then()
			.statusCode(401);
		} else {
			System.out.println("Failed to connect to the server.");
		}
	}

	@Test
	@Severity(SeverityLevel.CRITICAL)
	public void testGETInvalidApiKey() {
		Response response = getUser(VALID_USER_ID, INVALID_API_KEY);

		if (response != null) {
			response.then()
			.statusCode(401);
		} else {
			System.out.println("Failed to connect to the server.");
		}
	}

	@Test
	@Severity(SeverityLevel.CRITICAL)
	public void testPOSTWithValidKey() {
		Response response = createUser(JSON_REQUEST_BODY, API_KEY);

		if (response != null) {
			response.then()
			.statusCode(200)
			.body("status", equalTo("Success"))
			.body("data.userId", notNullValue())
			.body("data.status", equalTo("active"))
			.body("data.firstName", equalTo("Jane"))
			.body("data.lastName", equalTo("Johns"))
			.body("data.dateOfBirth", equalTo("1985-06-04"))
			.body("data.email", equalTo("jane123@email.com"))
			.body("data.rating", equalTo(8));
		} else {
			System.out.println("Failed to connect to the server.");
		}
	}

	@Test
	@Severity(SeverityLevel.CRITICAL)
	public void testPOSTWithNoAuthKey() {
		Response response = createUser(JSON_REQUEST_BODY, "");

		if (response != null) {
			response.then()
			.statusCode(401);
		} else {
			System.out.println("Failed to connect to the server.");
		}
	}

	@Test
	@Severity(SeverityLevel.CRITICAL)
	public void testPOSTWithInvalidKey() {
		Response response = createUser(JSON_REQUEST_BODY, INVALID_API_KEY);

		if (response != null) {
			response.then()
			.statusCode(401);
		} else {
			System.out.println("Failed to connect to the server.");
		}
	}
}
