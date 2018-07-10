package me.superblaubeere27.jobf.util.values;

import com.google.gson.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ConfigManager {
    private static Gson gson = new Gson();

    public static String generateConfig(Configuration config, boolean prettyPrint) {
        if (prettyPrint) {
            gson = new GsonBuilder().setPrettyPrinting().create();
        } else {
            gson = new GsonBuilder().create();
        }

        final JsonObject jsonObject = new JsonObject();

        config.addToJsonObject(jsonObject);

        HashMap<String, ArrayList<Value<?>>> ownerValueMap = new HashMap<>();

        for (Value<?> value : ValueManager.getValues()) {
            if (!ownerValueMap.containsKey(value.getOwner())) {
                ownerValueMap.put(value.getOwner(), new ArrayList<>());
            }
            ownerValueMap.get(value.getOwner()).add(value);
        }

        for (Map.Entry<String, ArrayList<Value<?>>> entry : ownerValueMap.entrySet()) {
            final JsonObject moduleJson = new JsonObject();

            for (final Value value : entry.getValue()) {
                if (value.getObject() instanceof Number) {
                    moduleJson.addProperty(value.getName(), (Number) value.getObject());
                } else if (value.getObject() instanceof Boolean) {
                    moduleJson.addProperty(value.getName(), (Boolean) value.getObject());
                } else if (value.getObject() instanceof String) {
                    moduleJson.addProperty(value.getName(), (String) value.getObject());
                }
            }

            jsonObject.add(entry.getKey(), moduleJson);
        }

        return gson.toJson(jsonObject);
    }

    public static Configuration loadConfig(String config) {
        final JsonElement jsonElement = gson.fromJson(config, JsonElement.class);

        if (jsonElement instanceof JsonNull) {
            throw new IllegalArgumentException("JsonObject isn't valid");
        }

        final JsonObject jsonObject = (JsonObject) jsonElement;

        Configuration configuration = Configuration.fromJsonObject(jsonObject);

        HashMap<String, ArrayList<Value<?>>> ownerValueMap = new HashMap<>();

        for (Value<?> value : ValueManager.getValues()) {
            if (!ownerValueMap.containsKey(value.getOwner())) {
                ownerValueMap.put(value.getOwner(), new ArrayList<>());
            }
            ownerValueMap.get(value.getOwner()).add(value);
        }

        for (Map.Entry<String, ArrayList<Value<?>>> entry : ownerValueMap.entrySet()) {
            if (!jsonObject.has(entry.getKey())) {
                continue;
            }

            final JsonElement moduleElement = jsonObject.get(entry.getKey());

            if (moduleElement instanceof JsonNull) {
                continue;
            }

            final JsonObject moduleJson = (JsonObject) moduleElement;

            for (final Value value : entry.getValue()) {
                try {
                    if (!moduleJson.has(value.getName())) {
                        continue;
                    }

                    if (value.getObject() instanceof Float) {
                        value.setObject(moduleJson.get(value.getName()).getAsFloat());
                    } else if (value.getObject() instanceof Double) {
                        value.setObject(moduleJson.get(value.getName()).getAsDouble());
                    } else if (value.getObject() instanceof Integer) {
                        value.setObject(moduleJson.get(value.getName()).getAsInt());
                    } else if (value.getObject() instanceof Long) {
                        value.setObject(moduleJson.get(value.getName()).getAsLong());
                    } else if (value.getObject() instanceof Byte) {
                        value.setObject(moduleJson.get(value.getName()).getAsByte());
                    } else if (value.getObject() instanceof Boolean) {
                        value.setObject(moduleJson.get(value.getName()).getAsBoolean());
                    } else if (value.getObject() instanceof String) {
                        value.setObject(moduleJson.get(value.getName()).getAsString());
                    }
                } catch (Throwable e) {
                    System.out.println(value.getName() + ": " + e);
                }
            }
        }
        return configuration;
    }
}