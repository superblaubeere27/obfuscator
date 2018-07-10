package me.superblaubeere27.jobf.utils;

public class Template {
    private String name;
    private String json;

    Template(String name, String json) {
        this.name = name;
        this.json = json;
    }

    public String getName() {
        return name;
    }

    public String getJson() {
        return json;
    }

    @Override
    public String toString() {
        return String.format("%s", name);
    }
}
