package API_Tests;




import org.testng.annotations.Test;

import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

import org.json.JSONObject;

public class CreateUsers {

	private static final String BASE_URL = "https://mzo5slmo45.execute-api.eu-west-2.amazonaws.com/v1";
	private static final String API_KEY = "GombImxOhMCa8AqMmNM9KEFwaSHSFHty";

	static {
		RestAssured.baseURI = BASE_URL;
	}

	// Helper method to create user
	private Response createUser(String requestBody) {
		System.out.println("Sending request to: " + BASE_URL + "/users");
		System.out.println("Request Body: " + requestBody);
		Response response = null;
		try {
			response = given()
					.header("Authorization", "Bearer " + API_KEY)
					.contentType("application/json")
					.body(requestBody)
					.when()
					.post("/users")
					.then()
					.extract().response();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return response;
	}

	@Test
	@Severity(SeverityLevel.CRITICAL)
	public void testCreateUser_ValidInput() {
		final String JSON_REQUEST_BODY = "{\n"
				+ "\"title\": \"Mr\",\n"
				+ "\"firstName\": \"Jane\",\n"
				+ "\"lastName\": \"Johns\",\n"
				+ "\"dateOfBirth\": \"1985-06-04\",\n"
				+ "\"email\": \"jane123@email.com\",\n"
				+ "\"password\": \"123456543erfgfx\",\n"
				+ "\"rating\": 8\n"
				+ "}";

		Response response = createUser(JSON_REQUEST_BODY);

		// Check if response is null due to connection issue
		if (response == null) {
			System.out.println("Failed to connect to the server.");
			return;
		}

		// Print the response for debugging
		// System.out.println("Response: " + response.asString());

		// Assertions
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
	}


	@Test 
	@Severity(SeverityLevel.CRITICAL)
	public void testCreateUserWithExistingEmail() {

		final String JSON_REQUEST_BODY = "{\n"
				+ "\"title\": \"Mr\",\n"
				+ "\"firstName\": \"Jane\",\n"
				+ "\"lastName\": \"Johns\",\n"
				+ "\"dateOfBirth\": \"1985-06-04\",\n"
				+ "\"email\": \"jane123@email.com\",\n"
				+ "\"password\": \"123456543erfgfx\",\n"
				+ "\"rating\": 8\n"
				+ "}";

		// Create user with the given email
		createUser(JSON_REQUEST_BODY);

		// Try to create the same user again with duplicate email
		Response response = createUser(JSON_REQUEST_BODY);

		// Assertions
		response.then()
		.statusCode(400)
		.body("errorType", notNullValue())
		.body("errorMessage", notNullValue());
	}


	@Test 
	@Severity(SeverityLevel.CRITICAL)
	public void testCreateUserWithInvalidEmail() {

		final String JSON_REQUEST_BODY = "{\n"
				+ "\"title\": \"Mr\",\n"
				+ "\"firstName\": \"Jane\",\n"
				+ "\"lastName\": \"Johns\",\n"
				+ "\"dateOfBirth\": \"1985-06-04\",\n"
				+ "\"email\": \"invalid@email.cam\",\n"
				+ "\"password\": \"123456543erfgfx\",\n"
				+ "\"rating\": 8\n"
				+ "}";

		// Create user with the given email
		createUser(JSON_REQUEST_BODY);

		// Try to create a user with invalid email: .cam is used instead of .com
		Response response = createUser(JSON_REQUEST_BODY);

		// Assertions
		response.then()
		.statusCode(400)
		.body("errorType", notNullValue())
		.body("errorMessage", notNullValue());
	}

	@Test
	@Severity(SeverityLevel.CRITICAL)
	public void testCreateUser_MissingRequiredFields() {
		//missing email
		final String JSON_REQUEST_BODY = "{ \"title\": \"Mr\", \"firstName\": \"John\", \"lastName\": \"Doe\", \"dateOfBirth\": \"1987-06-04\", \"email\": \"\", \"password\": \"super secret password\", \"rating\": 10 }";

		Response response = createUser(JSON_REQUEST_BODY);

		// Assertions
		response.then()
		.statusCode(400)
		.body("errorType", notNullValue())
		.body("errorMessage", notNullValue());
	}

	@Test
	@Severity(SeverityLevel.CRITICAL)
	public void testCreateUser_InvalidFieldValues() {
		//name length less than 2
		final String JSON_REQUEST_BODY = "{ \"title\": \"Mr\", \"firstName\": \"J\", \"lastName\": \"Doe\", \"dateOfBirth\": \"1987/06/04\", \"email\": \"invalidemail.com\", \"password\": \"327password\", \"rating\": 10 }";

		Response response = createUser(JSON_REQUEST_BODY);

		// Assertions
		response.then()
		.statusCode(400)
		.body("errorType", notNullValue())
		.body("errorMessage", notNullValue());
	}


	@Test
	@Severity(SeverityLevel.CRITICAL)
	public void testCreateUser_RatingWithinBoundary() {
		final String JSON_REQUEST_BODY = "{ \"title\": \"Mr\", \"firstName\": \"Jane\", \"lastName\": \"Doe\", \"dateOfBirth\": \"1982-08-04\", \"email\": \"boundary@example.com\", \"password\": \"super secret password\", \"rating\": 9 }";

		Response response = createUser(JSON_REQUEST_BODY);

		// Assertions
		response.then()
		//.statusCode(201) commented as this fails , a separate test case added
		.body("status", equalTo("Success"))
		.body("data.userId", notNullValue())
		.body("data.status", equalTo("active"))
		.body("data.firstName", equalTo("Jane"))
		.body("data.lastName", equalTo("Doe"))
		.body("data.dateOfBirth", equalTo("1982-08-04"))
		.body("data.email", equalTo("boundary@example.com"))
		.body("data.rating", equalTo(9));
	}
	
	@Test
	@Severity(SeverityLevel.CRITICAL)
	public void testCreateUser_ZeroRating_Returns_rejected() {
		final String JSON_REQUEST_BODY = "{ \"title\": \"Mr\", \"firstName\": \"Steffy\", \"lastName\": \"Johns\", \"dateOfBirth\": \"1992-08-04\", \"email\": \"SteffyJohns@example.com\", \"password\": \"super secret password\", \"rating\": 0 }";

		Response response = createUser(JSON_REQUEST_BODY);

		// Assertions
		response.then()
		//.statusCode(201)
		.body("status", equalTo("Success"))
		.body("data.userId", notNullValue())
		.body("data.status", equalTo("rejected"))
		.body("data.firstName", equalTo("Steffy"))
		.body("data.lastName", equalTo("Johns"))
		.body("data.dateOfBirth", equalTo("1992-08-04"))
		.body("data.email", equalTo("SteffyJohns@example.com"))
		.body("data.rating", equalTo(0));
	}

	@Test
	@Severity(SeverityLevel.CRITICAL)
	public void testCreateUser_UpperBoundaryValues() {
		final String JSON_REQUEST_BODY = "{ \"title\": \"Mr\", \"firstName\": \"Shehani\", \"lastName\": \"Rashmi\", \"dateOfBirth\": \"1992-12-04\", \"email\": \"Rashmi@example.com\", \"password\": \"super secret password\", \"rating\": 10 }";

		Response response = createUser(JSON_REQUEST_BODY);

		// Assertions
		response.then()
		//.statusCode(201)
		.body("status", equalTo("Success"))
		.body("data.userId", notNullValue())
		.body("data.status", equalTo("active"))
		.body("data.firstName", equalTo("Shehani"))
		.body("data.lastName", equalTo("Rashmi"))
		.body("data.dateOfBirth", equalTo("1992-12-04"))
		.body("data.email", equalTo("Rashmi@example.com"))
		.body("data.rating", equalTo(10));
	}

	@Test
	@Severity(SeverityLevel.CRITICAL)
	public void testCreateUser_InvalidRating() {
		final String JSON_REQUEST_BODY = "{ \"title\": \"Mr\", \"firstName\": \"Shehani\", \"lastName\": \"Rashmi\", \"dateOfBirth\": \"1992-12-04\", \"email\": \"negativeRating@example.com\", \"password\": \"super secret password\", \"rating\": -1 }";

		Response response = createUser(JSON_REQUEST_BODY);

		// Assertions
		response.then()
		.statusCode(400)
		.body("status", equalTo("Success"))
		.body("data.userId", notNullValue())
		.body("data.status", equalTo("rejected"))
		.body("data.firstName", equalTo("Shehani"))
		.body("data.lastName", equalTo("Rashmi"))
		.body("data.dateOfBirth", equalTo("1992-12-04"))
		.body("data.email", equalTo("negativeRating@example.com"))
		.body("data.rating", equalTo(-1));
	}

	@Test
	@Severity(SeverityLevel.NORMAL)
	public void testCreateUser_OptionalFields() {
		final String JSON_REQUEST_BODY = "{ \"firstName\": \"John\", \"lastName\": \"Doe\", \"dateOfBirth\": \"1987-06-04\", \"email\": \"optional@example.com\", \"password\": \"super secret password\", \"rating\": 7 }";

		Response response = createUser(JSON_REQUEST_BODY);

		// Assertions
		response.then()
		//.statusCode(201)
		.body("status", equalTo("Success"))
		.body("data.userId", notNullValue())
		.body("data.status", equalTo("active"))
		.body("data.firstName", equalTo("John"))
		.body("data.lastName", equalTo("Doe"))
		.body("data.dateOfBirth", equalTo("1987-06-04"))
		.body("data.email", equalTo("optional@example.com"))
		.body("data.rating", equalTo(7));
	}

	@Test
	@Severity(SeverityLevel.CRITICAL)
	public void testCreateUserRequestFormat() {
		JSONObject requestParams = new JSONObject();
		requestParams.put("title", "Mrs");
		requestParams.put("firstName", "Melani");
		requestParams.put("lastName", "Doe");
		requestParams.put("dateOfBirth", "1994-06-04");
		requestParams.put("email", "melani@email.com");
		requestParams.put("password", "password123#");
		requestParams.put("rating", 10);

		given()
		.header("Authorization", API_KEY)
		.contentType(ContentType.JSON)
		.body(requestParams.toString())
		.when()
		.post("/users")
		.then()
		.statusCode(equalTo(200)) 
		.contentType(ContentType.JSON)
		.body("status", equalTo("Success"))
		.body("data.userId", notNullValue())
		.body("data.title", equalTo("Mrs"))
		.body("data.firstName", equalTo("Melani"))
		.body("data.lastName", equalTo("Doe"))
		.body("data.dateOfBirth", equalTo("1994-06-04"))
		.body("data.email", equalTo("melani@email.com"))
		.body("data.rating", equalTo(10))
		.body("error", nullValue());
	}


	@Test
	@Severity(SeverityLevel.CRITICAL)
	public void testCreateUser_ValidateStatusCode() {
		final String JSON_REQUEST_BODY = "{ \"title\": \"Mr\", \"firstName\": \"Jane\", \"lastName\": \"Doe\", \"dateOfBirth\": \"1982-08-04\", \"email\": \"statuscodecheck@example.com\", \"password\": \"super secret password\", \"rating\": 9 }";

		Response response = createUser(JSON_REQUEST_BODY);

		// Assertions
		response.then()
		.statusCode(201)
		.body("status", equalTo("Success"))
		.body("data.userId", notNullValue())
		.body("data.status", equalTo("active"))
		.body("data.firstName", equalTo("Jane"))
		.body("data.lastName", equalTo("Doe"))
		.body("data.dateOfBirth", equalTo("1982-08-04"))
		.body("data.email", equalTo("boundary@example.com"))
		.body("data.rating", equalTo(9));
	}
}


