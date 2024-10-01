import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.response.ResponseBody;
import io.restassured.specification.RequestSpecification;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;

public class RestAssuredGetTest {

    @Before
    public void setUp(){
        RestAssured.baseURI = "https://restful-booker.herokuapp.com";
    }


    @Test
    public void testGetPing(){
        //building request
        RequestSpecification request = RestAssured.given();

        //hit the server
        Response response = request.get("/ping");


        //evaluate
        Assert.assertEquals(response.getStatusCode(), 201);
        Assert.assertEquals(response.asString(), "Created");
    }

    @Test
    public void getPing2() {
        //building request
        RequestSpecification request = RestAssured.given();

        //hit the server
        request.when()
                .get("/ping")
                .then().
                //evaluate
                        statusCode(201).
                body(equalTo("Created"));
    }

    @Test
    public void getPingUsingBuilder() {
        //building request
        RequestSpecBuilder builder = new RequestSpecBuilder();
        builder.setBaseUri("https://restful-booker.herokuapp.com");
        RequestSpecification request = RestAssured.given().spec(builder.build());

        //hit the server
        Response response = request.get("/ping");

        //evaluate
        System.out.println(response.getStatusCode());
        response.getBody().prettyPrint();

        Assert.assertEquals(response.getStatusCode(), 201);
        Assert.assertEquals(response.getBody().asString(), "Created");

    }

    @Test
    public void getIds() throws ParseException {
        //building request
        RequestSpecification request = RestAssured.given();

        //hit the server
        Response response = request.get("/booking");

        // Deserialization
        //evaluate
        ResponseBody body = response.getBody();
        JsonPath jsonPath = response.jsonPath();

        //Examples
        //position with JsonPath
        jsonPath.get("[0]");
        jsonPath.get("[0].bookingid");

        //all
        jsonPath.get("$");
        jsonPath.get();
        body.jsonPath().get("$");


        //Response body in string
        response.getBody().asString();


        //Evaluate - Deserialization JSONArray
        JSONParser parser = new JSONParser();
        JSONArray jArray = (JSONArray) parser.parse(response.asString());

        for(Object x : jArray){
            System.out.println(x.toString());
        }


        Assert.assertEquals(response.getStatusCode(), 200);
        Assert.assertTrue(jsonPath.get("[0].bookingid").toString()!=null);


        RestAssured.reset();

        //hit the server
        String firstBookingId = jsonPath.get("bookingid[0]").toString();
        response = request.get(String.format("/booking/%s", firstBookingId));



        //Evaluate -  - Deserialization JSONObject
        JSONObject jsonResponse = (JSONObject) parser.parse(response.asString());

        Assert.assertTrue(jsonResponse.containsKey("additionalneeds"));
        String bookingId = jsonResponse.get("additionalneeds").toString();
        Assert.assertTrue(StringUtils.isNotEmpty(bookingId));

        //- Deserialization JsonPath
        jsonPath = response.jsonPath();
        jsonPath.get("bookingdates.checkin");
        jsonPath.get("totalprice");
        response.jsonPath().get("bookingdates.checkin");

    }

}
