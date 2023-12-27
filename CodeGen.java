package Generator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import Generator.Template_Reader;

/**
 * CodeGen
 */
public class CodeGen {

    String config;

    public CodeGen(String conf){
        this.config=conf;
    }

    public String[] listAllClasses(Template_Reader reader) throws Exception{
        JsonObject conf=reader.getObject(config+"\\framework-config.json");
        String modelPath=conf.get("models").getAsString();
        File file=new File(this.config+modelPath);
        return file.list(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                if (name.toLowerCase().endsWith(reader.getSyntax().get("file_extension"))) {
                    return true;
                }
                return false;
            }  
        });
    }

    public void codeGenerator(int componentType, String type) throws Exception{
        Template_Reader reader=new Template_Reader(type);
        if (componentType==1) {
            String[] models=this.listAllClasses(reader);
            for (String file : models) {
                file=file.replace(reader.getSyntax().get("file_extension"), "");
                generateController(reader,type,file);
            }
        } else if (componentType==0) {
            
        }
    }

    public void generateController(Template_Reader reader,String type,String className) throws Exception{
        File dir=new File(this.config+"/Controllers");
        if (dir.exists()==false) {
            dir.mkdir();
        }
        File file=new File(this.config+"/Controllers/"+className+"Controller"+reader.getSyntax().get("file_extension"));
        if (file.exists()) {
            return;
        }
        String controllerString=this.generateControllerString(reader,className);
        FileWriter fileWriter = new FileWriter(file);
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
        bufferedWriter.write(controllerString);
        bufferedWriter.close();
    }

    public String generateControllerString(Template_Reader reader,String className) throws Exception{
        String template=this.resolve_syntax(reader,"Templates/Controllers/base.ou");
        template=this.resolve_extends(reader,template);
        template=this.addAnnotations(reader,template,className);
        template=this.generateImportString(reader,template);
        template=template.replace("{package}", "Controllers");
        template=template.replace("{classname}", className);
        template=addRestMethods(reader, template, className);
        return template;
    }

    public String resolve_syntax(Template_Reader reader,String path) throws Exception{
        String template=reader.readTemplate(path);
        for (Map.Entry<String, String> entry : reader.getSyntax().entrySet()) {
            template=template.replace("$$"+entry.getKey()+"$$", entry.getValue());            
        }
        return template;
    }

    public String resolve_extends(Template_Reader reader,String syntax) throws Exception{
        if ((boolean)reader.getSpec().get("extendable")==true) {
            syntax=syntax.replace("{extendable}", reader.getSyntax().get("extends")+" "+reader.getSpec().get("baseclass"));
        } else {
            syntax=syntax.replace("{extendable}", "");
        }
        return syntax;
    }

    public String addAnnotations(Template_Reader reader,String syntax,String className) throws Exception{
        if ((boolean)reader.getSpec().get("annotable")==true) {
            for (Map.Entry<String, String> entry : reader.getAnnotation().entrySet()) {
                syntax=syntax.replace("#"+entry.getKey()+"#", entry.getValue());            
            }
            syntax=syntax.replace("{model}", "/"+className+"s");   
        } else {
            for (Map.Entry<String, String> entry : reader.getAnnotation().entrySet()) {
                syntax=syntax.replace("#"+entry.getKey()+"#", "");            
            }
        }
        return syntax;
    }

    public String generateImportString(Template_Reader reader,String syntax) throws Exception{
        StringBuilder imports = new StringBuilder();    
        List<String> imp=(ArrayList<String>)reader.getSpec().get("imports");
        for (int i = 0; i < imp.size(); i++) {
            imports.append(reader.getSyntax().get("import"))
                    .append(" ")
                    .append(imp.get(i)+reader.getSyntax().get("line_end"))
                    .append("\n");
        }
        syntax=syntax.replace("{imports}", imports.toString());

        return syntax;
    }

    public String arrayToString(String[] array){
        StringBuilder builder=new StringBuilder();
        for (int i = 0; i < array.length; i++) {
            builder.append(array[i]);
            if (i<array.length-1) {
                builder.append(",");
            }
        }
        return builder.toString();
    }

    public String addRestMethods(Template_Reader reader,String template, String className) throws Exception{
        template=template.replace("{get_all_method}", addMethod(reader, className, "getAll"+className, "GET"));
        template=template.replace("{get_by_id_method}", addMethod(reader, className, "get"+className+"ById", "GET_ID","String id"));
        template=template.replace("{insert_method}", addMethod(reader, className, "Insert"+className, "POST",className+" "+className));
        template=template.replace("{update_method}", addMethod(reader, className, "Update"+className, "PUT",className+" "+className));
        template=template.replace("{delete_method}", addMethod(reader, className, "Delete"+className, "DELETE",className+" "+className));
        return template;
    }

    public String addMethod(Template_Reader reader,String className,String name,String annotation,String...args) throws Exception{
        String template=this.resolve_syntax(reader, "Templates/method.ou");
        template=template.replace("{name}", name);
        template=template.replace("{args}", this.arrayToString(args));
        return template;
    }

    public String getConfig() {
        return config;
    }
}