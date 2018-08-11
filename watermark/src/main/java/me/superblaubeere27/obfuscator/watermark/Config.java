package me.superblaubeere27.obfuscator.watermark;

import com.google.gson.JsonObject;

import java.util.Random;

public class Config {
    private static final Random random = new Random();

    private final String key;
    private final String magicBytes;

    public Config(String key, String magicBytes) {
        this.key = key;
        this.magicBytes = magicBytes;
    }

    public static Config generateConfig() {
        return new Config(randomString(random.nextInt(80) + 50), randomString(random.nextInt(5) + 1));
    }

    public static Config fromJson(JsonObject object) {
        if (!object.has("key")) throw new IllegalArgumentException("JsonObject hasn't 'key'");
        if (!object.has("magicBytes")) throw new IllegalArgumentException("JsonObject hasn't 'magicBytes'");

        return new Config(object.get("key").getAsString(), object.get("magicBytes").getAsString());
    }

    private static String randomString(int i) {
        StringBuilder sb = new StringBuilder();

        for (int i1 = 0; i1 < i; i1++) {
            sb.append((char) (random.nextInt(126 - 32) + 32));
        }

        return sb.toString();
    }

    public JsonObject toJsonObject() {
        JsonObject obj = new JsonObject();

        obj.addProperty("key", key);
        obj.addProperty("magicBytes", magicBytes);

        return obj;
    }

    public String getKey() {
        return key;
    }

    public String getMagicBytes() {
        return magicBytes;
    }

    @Override
    public String toString() {
        return "Config{" +
                "key='" + key + '\'' +
                ", magicBytes='" + magicBytes + '\'' +
                '}';
    }
}
