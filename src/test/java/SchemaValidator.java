import com.github.fge.jsonschema.SchemaVersion;
import com.github.fge.jsonschema.cfg.ValidationConfiguration;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.junit.Before;
import org.junit.Test;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchema;


public class SchemaValidator {

    RestAssuredHelper restAssuredHelper = new RestAssuredHelper();

    @Before
    public void setUp(){
        RestAssured.baseURI = "https://restful-booker.herokuapp.com";
    }

    public JsonSchemaFactory jsf(){
        JsonSchemaFactory jsonSchemaFactory = JsonSchemaFactory.newBuilder()
                .setValidationConfiguration(
                        ValidationConfiguration.newBuilder()
                                .setDefaultVersion(SchemaVersion.DRAFTV4).freeze())
                .freeze();

        return jsonSchemaFactory;
    }

    @Test
    public void createCredentialSchemaValidation()  {
        RequestSpecification request = RestAssured.given();
        request.header("Content-Type", "application/json");
        String credentialsBodyRequest = restAssuredHelper.setCredentials().toJSONString();
        request.body(credentialsBodyRequest);

        Response response = request.post("/auth");

        response.then().assertThat()
                .body(matchesJsonSchema(restAssuredHelper.getSchemaBody("CredentialResponseSchema.json"))
                        .using(jsf()));
    }

    @Test
    public void createBookingSchemaValidation() {
        RequestSpecification request = RestAssured.given();
        request.header("Content-Type", "application/json");

        //SERIALIZACION
        String body = restAssuredHelper.getFileBody("CreateBooking.json");
        body = restAssuredHelper.insertParams(body);
        request.body(body);

        //HIT THE ENDPOINT
        Response response = request.post("/booking");

        response.then().assertThat()
                .body(matchesJsonSchema(restAssuredHelper.getSchemaBody("CreateBookingSchema.json"))
                        .using(jsf()));
    }
}
