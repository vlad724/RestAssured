import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;
import org.testng.SkipException;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RestAssuredHelper {
    public static JSONObject testData = new JSONObject();

    public  RestAssuredHelper(){
        testData.put("username", "admin");
        testData.put("password", "password123");

        testData.put("firstname", "Tom Sawyer");
        testData.put("totalprice", 60000);
        testData.put("depositpaid", true);
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

    public String getCurrentPath() {
        return Paths.get("").toAbsolutePath().toString();
    }


    public String insertParams(String stringData) {
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
