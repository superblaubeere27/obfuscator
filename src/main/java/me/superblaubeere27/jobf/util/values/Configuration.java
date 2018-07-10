package me.superblaubeere27.jobf.util.values;

import com.google.gson.JsonObject;

public class Configuration {
    private String input;
    private String output;
    private String script;

    public Configuration(String input, String output, String script) {
        this.input = input;
        this.output = output;
        this.script = script;
    }

    static Configuration fromJsonObject(JsonObject obj) {
        String input = "";
        String output = "";
        String script = null;

        if (obj.has("input")) {
            input = obj.get("input").getAsString();
        }
        if (obj.has("output")) {
            output = obj.get("output").getAsString();
        }
        if (obj.has("script")) {
            script = obj.get("script").getAsString();
        }

        return new Configuration(input, output, script);
    }

    void addToJsonObject(JsonObject jsonObject) {
        jsonObject.addProperty("input", input);
        jsonObject.addProperty("output", output);
        jsonObject.addProperty("script", script);
    }

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }

    public String getScript() {
        return script;
    }

    public void setScript(String script) {
        this.script = script;
    }
}
