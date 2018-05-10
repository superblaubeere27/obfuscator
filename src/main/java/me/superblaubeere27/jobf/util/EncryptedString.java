package me.superblaubeere27.jobf.util;

public class EncryptedString {
    private String string;
    private String key;

    public EncryptedString(String string, String key) {
        this.string = string;
        this.key = key;
    }

    public String getString() {
        return string;
    }

    public void setString(String string) {
        this.string = string;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
