package test;

import Generator.CodeGen;

public class Main {
    public static void main(String[] args) {
        try {
            CodeGen gen=new CodeGen(args[0]);
            gen.codeGenerator(1, "spring");
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }
}
