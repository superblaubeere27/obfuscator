package me.superblaubeere27.jobf;

import com.google.gson.JsonObject;

public class Configuration {
    public boolean isFlowObfuscatorEnabled;
    public boolean isInformationRemoverEnabled;
    public boolean isNumberObfuscatorEnabled;
    public boolean isHiderEnabled;
    public boolean isShuffleMembersEnabled;
    public boolean isStaticInitializionProtectorEnabled;
    public boolean isStringEncryptionEnabled;
    public boolean isReferenceProxyEnabled;

    public Configuration fromJson(JsonObject object) {
        if (object.has("FlowObfuscator")) {
            JsonObject obj1 = object.getAsJsonObject("FlowObfuscator");
            isFlowObfuscatorEnabled = obj1.has("enabled") && obj1.get("enabled").getAsBoolean();
        }
        if (object.has("ReferenceProxy")) {
            JsonObject obj1 = object.getAsJsonObject("ReferenceProxy");
            isReferenceProxyEnabled = obj1.has("enabled") && obj1.get("enabled").getAsBoolean();
        }

        if (object.has("InformationRemover")) {
            JsonObject obj1 = object.getAsJsonObject("InformationRemover");
            isInformationRemoverEnabled = obj1.has("enabled") && obj1.get("enabled").getAsBoolean();
        }
        if (object.has("NumberObfuscation")) {
            JsonObject obj1 = object.getAsJsonObject("NumberObfuscation");
            isNumberObfuscatorEnabled = obj1.has("enabled") && obj1.get("enabled").getAsBoolean();
        }
        if (object.has("Hider")) {
            JsonObject obj1 = object.getAsJsonObject("Hider");
            isHiderEnabled = obj1.has("enabled") && obj1.get("enabled").getAsBoolean();
        }
        if (object.has("StaticInitializion")) {
            JsonObject obj1 = object.getAsJsonObject("StaticInitializion");
            isStaticInitializionProtectorEnabled = obj1.has("enabled") && obj1.get("enabled").getAsBoolean();
        }
        if (object.has("StringEncryption")) {
            JsonObject obj1 = object.getAsJsonObject("StringEncryption");
            isStringEncryptionEnabled = obj1.has("enabled") && obj1.get("enabled").getAsBoolean();
        }
        return this;
    }

    public JsonObject toJson() {
        JsonObject object = new JsonObject();

        JsonObject flowObfuscatorObject = new JsonObject();
        flowObfuscatorObject.addProperty("enabled", isFlowObfuscatorEnabled);

        object.add("ReferenceProxy", flowObfuscatorObject);
        JsonObject refProxy = new JsonObject();
        refProxy.addProperty("enabled", isReferenceProxyEnabled);
        object.add("ReferenceProxy", refProxy);

        JsonObject informationRemover = new JsonObject();
        informationRemover.addProperty("enabled", isInformationRemoverEnabled);
        object.add("InformationRemover", informationRemover);

        JsonObject isNumberObfuscationRemover = new JsonObject();
        isNumberObfuscationRemover.addProperty("enabled", isNumberObfuscatorEnabled);
        object.add("NumberObfuscation", isNumberObfuscationRemover);

        JsonObject hiderObject = new JsonObject();
        hiderObject.addProperty("enabled", isHiderEnabled);
        object.add("Hider", hiderObject);

        JsonObject shuffleProcessor = new JsonObject();
        shuffleProcessor.addProperty("enabled", isShuffleMembersEnabled);
        object.add("Hider", shuffleProcessor);

        JsonObject staticInitializion = new JsonObject();
        staticInitializion.addProperty("enabled", isStaticInitializionProtectorEnabled);
        object.add("StaticInitializion", staticInitializion);

        JsonObject stringencryption = new JsonObject();
        stringencryption.addProperty("enabled", isStringEncryptionEnabled);
        object.add("StringEncryption", stringencryption);

        return object;
    }
}
