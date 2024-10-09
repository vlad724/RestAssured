import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import lombok.extern.java.Log;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

@Log
public class SmokeTest {

    RestAssuredHelper restAssuredHelper = new RestAssuredHelper();

    public JSONObject testData = new JSONObject();

    String ping = null;


    @BeforeClass
    public void setUp(){
        RestAssured.baseURI = "https://restful-booker.herokuapp.com";
    }

    @Test
    public void test1GetPingUsingBuilder() {
        //building request
        RequestSpecBuilder builder = new RequestSpecBuilder();
        builder.setBaseUri("https://restful-booker.herokuapp.com");
        RequestSpecification request = RestAssured.given().spec(builder.build());

        //hit the server
        Response response = request.get("/ping");

        //evaluate
        System.out.println(response.getStatusCode());
        response.getBody().prettyPrint();
        ping = response.getBody().asString();

        Assert.assertEquals(response.getStatusCode(), 201);
        Assert.assertEquals(ping, "Created");

    }

    @Test
    public void test2PostCreateToken() throws ParseException {

        RequestSpecification request = RestAssured.given();
        request.header("Content-Type", "application/json");

        String credentialsBodyRequest = restAssuredHelper.setCredentials().toJSONString();
        request.body(credentialsBodyRequest);
        //request.body(restAssuredHelper.getFileBody("bad_credentials.json").toString());
        //request.body(restAssuredHelper.getFileBody("bad_credentials2.json").toString());

        //HIT THE ENDPOINT
        Response response = request.post("/auth");

        //REVIEW RESPONSE
        response.prettyPrint();
        Assert.assertEquals(response.getStatusCode(), 200);

        //DESERIALIZACION
        JSONParser parser = new JSONParser();
        JsonPath rawResponse= response.getBody().jsonPath();
        JSONObject jsonResponse = (JSONObject) parser.parse(response.asString());

        String token = rawResponse.get("token");
        if(StringUtils.isNotEmpty(token)){
            testData.put("token", token);
        }

        log.info(token);
        Assert.assertTrue(jsonResponse.containsKey("token"));
        Assert.assertTrue(StringUtils.isNotEmpty(token));


    }

    @Test
    public void test3PostCreateBooking() {
        RequestSpecification request = RestAssured.given();
        request.header("Content-Type", "application/json");
        request.header("Cookie", String.format("token=%s", testData.get("token")));

        //SERIALIZACION
        String body = restAssuredHelper.getFileBody("CreateBooking.json");
        //String body = restAssuredHelper.getFileBody("CreateBooking_BadRequest.json");
        //String body = restAssuredHelper.getFileBody("CreateBookingLongString.json");
        body = restAssuredHelper.insertParams(body);
        request.body(body);

        //HIT THE ENDPOINT
        Response response = request.post("/booking");

        //REVIEW RESPONSE
        response.prettyPrint();
        Assert.assertEquals(response.getStatusCode(), 200);

        //DESERIALIZACION
        JsonPath rawResponse= response.getBody().jsonPath();
        String bookingId = rawResponse.get("bookingid").toString();


        if(StringUtils.isNotEmpty(bookingId)){
            testData.put("bookingId", bookingId);
        }

        Assert.assertEquals(response.getStatusCode(), 200);
        Assert.assertTrue(StringUtils.isNotEmpty(bookingId));


    }


    @Test
    public void test3PutUpdateBooking() {

        RequestSpecification request = RestAssured.given();
        request.header("Content-Type", "application/json");
        request.header("Cookie", String.format("token=%s", testData.get("token").toString()));

        //SERIALIZACION
        String body = restAssuredHelper.getFileBody("UpdateBooking.json");
        body = restAssuredHelper.insertParams(body);
        request.body(body);

        //HIT THE ENDPOINT
        Response response = request.put(String.format("/booking/%s", testData.get("bookingId")));

        Assert.assertEquals(response.getStatusCode(), 200);
    }

}
