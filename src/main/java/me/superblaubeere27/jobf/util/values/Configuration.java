package me.superblaubeere27.jobf.util.values;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

public class Configuration {
    private String input;
    private String output;
    private String script;
    private List<String> libraries;

    public Configuration(String input, String output, String script, List<String> libraries) {
        this.input = input;
        this.output = output;
        this.script = script;
        this.libraries = libraries;
    }

    static Configuration fromJsonObject(JsonObject obj) {
        String input = "";
        String output = "";
        String script = null;
        List<String> libraries = new ArrayList<>();

        if (obj.has("input")) {
            input = obj.get("input").getAsString();
        }
        if (obj.has("output")) {
            output = obj.get("output").getAsString();
        }
        if (obj.has("script")) {
            script = obj.get("script").getAsString();
        }
        if (obj.has("libraries")) {
            JsonArray jsonArray = obj.getAsJsonArray("libraries");

            for (JsonElement jsonElement : jsonArray) {
                libraries.add(jsonElement.getAsString());
            }
        }

        return new Configuration(input, output, script, libraries);
    }

    void addToJsonObject(JsonObject jsonObject) {
        jsonObject.addProperty("input", input);
        jsonObject.addProperty("output", output);
        jsonObject.addProperty("script", script);

        JsonArray array = new JsonArray();

        for (String library : libraries) {
            array.add(library);
        }

        jsonObject.add("libraries", array);
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

    public List<String> getLibraries() {
        return libraries;
    }
}
