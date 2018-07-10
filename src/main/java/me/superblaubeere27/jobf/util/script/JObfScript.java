package me.superblaubeere27.jobf.util.script;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class JObfScript {

    private ScriptEngine jsEngine;

    public JObfScript(String script) {
        try {
            jsEngine = new ScriptEngineManager().getEngineByName("nashorn");
            jsEngine.eval(script);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to compile Script", e);
        }
    }

    public boolean remapClass(String className) {
        try {
            Invocable invocable = (Invocable) jsEngine;

            return (boolean) invocable.invokeFunction("isRemappingEnabledForClass", className);
        } catch (NoSuchMethodException e) {
            return true;
        } catch (ScriptException e) {
            e.printStackTrace();
            return true;
        }
    }

    public boolean isObfuscatorEnabled(String className) {
        try {
            Invocable invocable = (Invocable) jsEngine;

            return (boolean) invocable.invokeFunction("isObfuscatorEnabledForClass", className);
        } catch (NoSuchMethodException e) {
            return true;
        } catch (ScriptException e) {
            e.printStackTrace();
            return true;
        }
    }

}
