package restapi;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.util.List;
import java.util.Map;

public class practiceJSON {

    static List<Map<String, Object>> contents;

    public static void saveAll(String jsonMessage){
        try {
            Object result = parseJson(jsonMessage);
            assert result != null;
            contents = (List<Map<String, Object>>) result;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void save(String jsonMessage){
        Object result = parseJson(jsonMessage);
        assert result != null;
        Map content = (Map) result;
        contents.add(content);
    }

    public static Object parseJson(String jsonMessage){
        try {
            ScriptEngineManager sem = new ScriptEngineManager();
            ScriptEngine se = sem.getEngineByName("javascript");
            String script = "Java.asJSONCompatible(" + jsonMessage + ")";
            return se.eval(script);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    public static void main(String[] args) throws Exception {
        String url = "https://jsonplaceholder.typicode.com/users";
        String json = RestAPI.get(url);
        saveAll(json);

        for (Map<String, Object> content : contents) {
            System.out.println(content.get("id") + " " + content.get("name"));
        }

        System.out.println("\n인원 추가\n");

        url = "https://jsonplaceholder.typicode.com/users/7";
        json = RestAPI.get(url);
        save(json);

        for (Map<String, Object> content : contents) {
            System.out.println(content.get("id") + " " + content.get("name"));
        }


    }
}
