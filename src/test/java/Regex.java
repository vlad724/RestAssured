import org.junit.Test;

import java.util.regex.Pattern;
import java.util.regex.Matcher;


public class Regex {

    @Test
    public void regex1() {
        Pattern myPattern = Pattern.compile("\\w+");
        Matcher myMatcher = myPattern.matcher("=((4_geeks %&Academy$·$%$·$·");
        while (myMatcher.find()) {
            System.out.println("Encontrado: " + myMatcher.group() + " en: " + myMatcher.start() + "," + myMatcher.end());
        }
    }

    @Test
    public void regex2() {
        String html = ">Jacksonville,</span> FL 32218-4002 </span></p>";
        //"</span>\\s(.*?)\\s</span></p>"
        //">(.*?)<!---->"
        Pattern myPattern = Pattern.compile(">(.*?)<");
        Matcher myMatcher = myPattern.matcher(html);

        while (myMatcher.find()) {
            System.out.println("Encontrado: " + myMatcher.group(1));
        }
    }

    @Test
    public void regex3() {
        String html = "\t<div _ngcontent-ebj-c342=\"\" class=\"cg-margin-bottom-sm cg-bold\"> Diagnosis Code </div><div _ngcontent-ebj-c342=\"\">O82</div>\n";
        Pattern myPattern = Pattern.compile("Diagnosis Code[^<]</div><div[^<]*>(.*?)</div>");
        Matcher myMatcher = myPattern.matcher(html);

        while (myMatcher.find()) {
            System.out.println("Encontrado: " + myMatcher.group(1));
        }
    }

    @Test
    public void regex6() {
        String text = "<div class=\"cscore_team icon-font-after \">\\n        <div class=\"cscore_truncate\">\\n            <span class=\"cscore_name cscore_name--long\">G. Russell </span>\\n            <span class=\"cscore_name cscore_name--short\">G. Russell </span>\\n            <span class=\"cscore_name cscore_name--abbrev\">G. Russell </span>\\n            <div class=\"cscore_teamName\">Mercedes</div>\\n        </div>\\n        \\n            <div class=\"cscore_teamName\">Mercedes</div>\\n        \\n        <div class=\"cscore_data\">\\n            <div class=\"cscore_score\">\\n                \\n                    <span class=\"cscore_laps\">";  // Tu cadena de texto completa

        Pattern pattern = Pattern.compile("<span class=\"cscore_name.*?\">(.*?)<\\/span>");
        Matcher matcher = pattern.matcher(text);

        while (matcher.find()) {
            System.out.println(matcher.group(1));
        }
    }


    @Test
    public void regex4() {
        String html = "{\n" +
                "  \"firstname\": \"{{firstname}}\",\n" +
                "  \"lastname\": \"Diaz Ortiz\",\n" +
                "  \"totalprice\": {{totalprice}},\n" +
                "  \"depositpaid\": {{depositpaid}},\n" +
                "  \"bookingdates\": {\n" +
                "    \"checkin\": \"2024-08-20\",\n" +
                "    \"checkout\": \"2024-08-30\"\n" +
                "  },\n" +
                "  \"additionalneeds\": \"No requierements\"\n" +
                "}";
        Pattern myPattern = Pattern.compile("\\{\\{(\\w+)}}");
        Matcher myMatcher = myPattern.matcher(html);

        while (myMatcher.find()) {
            System.out.println("Encontrado: " + myMatcher.group(1));
        }
    }
}



