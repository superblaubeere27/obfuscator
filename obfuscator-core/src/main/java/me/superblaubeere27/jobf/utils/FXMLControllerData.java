package me.superblaubeere27.jobf.utils;

import java.util.HashMap;

public class FXMLControllerData {

    private final HashMap<String, String> fieldsData;
    private final HashMap<String, String> methodsData;
    private final String originalClassName;
    private String obfuscatedClassName;

    public FXMLControllerData(String originalClassName) {
        this.fieldsData = new HashMap<>();
        this.methodsData = new HashMap<>();
        this.originalClassName = originalClassName;
        this.obfuscatedClassName = null;
    }

    public void addFieldData(String originalName, String obfuscatedName) {
        this.fieldsData.put(originalName, obfuscatedName);
    }

    public void addMethodData(String originalName, String obfuscatedName) {
        this.methodsData.put(originalName, obfuscatedName);
    }

    public HashMap<String, String> getFieldsData() {
        return fieldsData;
    }

    public HashMap<String, String> getMethodsData() {
        return methodsData;
    }

    public String getOriginalClassName() {
        return originalClassName;
    }

    public String getObfuscatedClassName() {
        return obfuscatedClassName;
    }

    public void setObfuscatedClassName(String obfuscatedClassName) {
        this.obfuscatedClassName = obfuscatedClassName;
    }
}
