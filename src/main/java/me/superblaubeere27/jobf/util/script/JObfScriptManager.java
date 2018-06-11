package me.superblaubeere27.jobf.util.script;

import javax.script.*;

public class JObfScriptManager {

    public static void main(String args[]) {
        try {
            ScriptEngine jsEngine = new ScriptEngineManager().getEngineByName("nashorn");
            ScriptContext context = jsEngine.getContext();
            jsEngine.eval("function isNameObfEnabled(className) {\n" +
                    "    if (!className.contains('x')) {\n" +
                    "        print('I will obfuscate ' + className);\n" +
                    "            return true;\n" +
                    "    }\n" +
                    "    return false;\n" +
                    "}");
            Invocable inv = (Invocable) jsEngine;
            try {
                System.out.println(inv.invokeFunction("isNameObfEnabled", "avg"));
                System.out.println(inv.invokeFunction("isNameObfEnabled", "xxx"));
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
        } catch (ScriptException e) {
            e.printStackTrace();
        }
    }

}
