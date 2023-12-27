package Generator;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

public class Template_Reader {
    String type;
    HashMap<String,String> syntax;
    HashMap<String,String> annotation;
    HashMap<String,Object> spec;

    public Template_Reader(){}
   
    public Template_Reader(String type) throws Exception{
        this.type=type;
        this.spec=this.getControllerSpecifications(type);
        initialize();
    }

    public JsonObject getObject(String path) throws Exception{
        BufferedReader reader = new BufferedReader(new FileReader(path));
            StringBuilder jsonString = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonString.append(line);
            }
            reader.close();
            JsonParser parser = new JsonParser();
            JsonObject jsonObject = parser.parse(jsonString.toString()).getAsJsonObject();
            return jsonObject;
    }

    public void initialize() throws Exception{
            JsonObject base=this.getObject("Templates/base_syntax.json");
            JsonObject annotation=this.getObject("Templates/annotations.json");
            Gson gson = new Gson();
            TypeToken<HashMap<String, String>> typeToken = new TypeToken<HashMap<String, String>>() {};
            this.syntax=gson.fromJson(base.get(this.spec.get("langage").toString()), typeToken.getType());
            this.annotation=gson.fromJson(annotation.get(this.spec.get("langage").toString()), typeToken.getType());   
    }

    public String readTemplate(String path) throws Exception{
            BufferedReader reader = new BufferedReader(new FileReader(path));
            StringBuilder jsonString = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonString.append(line).append("\n");
            }
            reader.close();
            return jsonString.toString();
    }

    public HashMap<String,Object> getControllerSpecifications(String controllerType) throws Exception{
        JsonObject object=this.getObject("Templates/Controllers/controllers_specifications.json");
        Gson gson = new Gson();
        TypeToken<HashMap<String, Object>> typeToken = new TypeToken<HashMap<String, Object>>() {};
        HashMap<String, Object> map=gson.fromJson(object.get(controllerType), typeToken.getType());
        return map;
    }

    public HashMap<String, String> getSyntax() {
        return syntax;
    }
    public HashMap<String, String> getAnnotation() {
        return annotation;
    }

    public HashMap<String, Object> getSpec() {
        return spec;
    }

    public String getType() {
        return type;
    }
    
}
