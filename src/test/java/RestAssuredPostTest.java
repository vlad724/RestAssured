import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import lombok.extern.java.Log;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.testng.SkipException;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Log
public class RestAssuredPostTest {

    public static JSONObject testData = new JSONObject();

    public  RestAssuredPostTest(){
        testData.put("username", "admin");
        testData.put("password", "password123");

        testData.put("firstname", "Tom");
        testData.put("totalprice", 60000);
        testData.put("depositpaid", true);
    }

    @Before
    public void setUp(){
        RestAssured.baseURI = "https://restful-booker.herokuapp.com";
    }

    String credentialsBodyRequest1 = "{\n" +
            "    \"username\" : \"admin\",\n" +
            "    \"password\" : \"password123\"\n" +
            "}";
    String badCredentials ="{'username': 'admin', 'password' : 'password123'}";




    @Test
    public void testPostCreateToken() throws ParseException {

        RequestSpecification request = RestAssured.given();
        request.header("Content-Type", "application/json");

        String credentialsBodyRequest = "{\"username\": \"admin\", \"password\" : \"password123\"}";
        request.body(credentialsBodyRequest);

        //HIT THE ENDPOINT
        Response response = request.post("/auth");

        //REVIEW RESPONSE
        response.prettyPrint();
        Assert.assertEquals(response.getStatusCode(), 200);

        //DESERIALIZACION
        JSONParser parser = new JSONParser();
        JsonPath rawResponse= response.getBody().jsonPath();
        JSONObject jsonResponse = (JSONObject) parser.parse(response.asString());

        String TOKEN = rawResponse.get("token").toString();
        log.info(TOKEN);

        Assert.assertTrue(jsonResponse.containsKey("token"));
        Assert.assertTrue(StringUtils.isNotEmpty(TOKEN));


    }

    public JSONObject setCredentials(){
        JSONObject credentials = new JSONObject();
        credentials.put("username", "admin");
        credentials.put("password", "password123");

        return credentials;
    }

    @Test
    public void testPostCreateToken2() throws ParseException {

        RequestSpecification request = RestAssured.given();
        request.header("Content-Type", "application/json");

        //SERIALIZACION
        JSONObject auth = new JSONObject();
        auth.put("username", "admin");
        auth.put("password", "password123");

        request.body(auth.toJSONString());
        //request.body(setCredentials().toJSONString());

        //HIT THE ENDPOINT
        Response response = request.post("/auth");

        //REVIEW RESPONSE
        response.prettyPrint();
        Assert.assertEquals(response.getStatusCode(), 200);

        //DESERIALIZACION
        JSONParser parser = new JSONParser();
        JsonPath rawResponse= response.getBody().jsonPath();
        JSONObject jsonResponse = (JSONObject) parser.parse(response.asString());

        String TOKEN = rawResponse.get("token").toString();
        log.info(TOKEN);

        Assert.assertTrue(jsonResponse.containsKey("token"));
        Assert.assertTrue(StringUtils.isNotEmpty(TOKEN));


    }


    @Test
    public void testPostCreateToken3() throws ParseException {

        RequestSpecification request = RestAssured.given();
        request.header("Content-Type", "application/json");

        //SERIALIZACION
        String body = getFileBody("credentials.json");
        body = insertParams(body);
        request.body(body);

        //HIT THE ENDPOINT
        Response response = request.post("/auth");

        //REVIEW RESPONSE
        response.prettyPrint();
        Assert.assertEquals(response.getStatusCode(), 200);

        //DESERIALIZACION
        JSONParser parser = new JSONParser();
        JsonPath rawResponse= response.getBody().jsonPath();
        JSONObject jsonResponse = (JSONObject) parser.parse(response.asString());

        String TOKEN = rawResponse.get("token").toString();
        log.info(TOKEN);

        Assert.assertTrue(jsonResponse.containsKey("token"));
        Assert.assertTrue(StringUtils.isNotEmpty(TOKEN));


    }

    @Test
    public void testPostCreateBooking1() throws ParseException {

        RequestSpecification request = RestAssured.given();
        request.header("Content-Type", "application/json");

        //SERIALIZACION
        String body = getFileBody("CreateBooking.json");
        body = insertParams(body);
        request.body(body);

        //HIT THE ENDPOINT
        Response response = request.post("/booking");

        //REVIEW RESPONSE
        response.prettyPrint();
        Assert.assertEquals(response.getStatusCode(), 200);

        //DESERIALIZACION
        JSONParser parser = new JSONParser();

        //DESERIALIZACION
        JsonPath rawResponse= response.getBody().jsonPath();
        String bookingId1 = rawResponse.get("bookingid").toString();
        String firstname = rawResponse.get("booking.firstname").toString();

        JSONObject jsonResponse = (JSONObject) parser.parse(response.asString());
        String bookingId2 = rawResponse.get("bookingid").toString();

        String bookingStr = rawResponse.get("booking").toString();
        Object booking1 = rawResponse.get("booking");
        String firstname1 = ((LinkedHashMap) booking1).get("firstname").toString();


        log.info(bookingId1);
        log.info(bookingId2);
        log.info(firstname);

        org.testng.Assert.assertEquals(response.getStatusCode(), 200);
        org.testng.Assert.assertTrue(jsonResponse.containsKey("bookingid"));
        org.testng.Assert.assertTrue(StringUtils.isNotEmpty(bookingId1));


    }


    public String getFileBody(String file){
        String bodyPath;
        try {
            bodyPath = new String(Files.readAllBytes(Paths.get(getCurrentPath()
                    +"/src/test/resources/body/" + file)));
        } catch (Exception e) {
            throw new SkipException("check configProperties or path variable " + e.getMessage());
        }

        return bodyPath;
    }

    public static String getCurrentPath() {
        return Paths.get("").toAbsolutePath().toString();
    }


    public static String insertParams(String stringData) {
        StringBuffer stringbuffer = new StringBuffer();
        Pattern pattern = Pattern.compile("\\$(\\w+)");
        Matcher matcher;
        String replacement = null;
        if (StringUtils.isEmpty(stringData)) {
            throw new SkipException("String is empty");
        }
        if (testData.isEmpty()) {
            throw new SkipException("Test Data is empty");
        }
        matcher = pattern.matcher(stringData);
        while (matcher.find()) {
            String varName = matcher.group(1);
            boolean keyExist = testData.containsKey(varName);
            replacement = keyExist ? testData.get(varName).toString() : stringData;

            if (StringUtils.isNotEmpty(replacement)) {
                matcher.appendReplacement(
                        stringbuffer,
                        matcher.group(1).replaceFirst(Pattern.quote(matcher.group(1)), replacement));
            }
        }
        if (StringUtils.isNotEmpty(stringbuffer)) {
            matcher.appendTail(stringbuffer);
            stringData = stringbuffer.toString();
        }
        return stringData;
    }
}
