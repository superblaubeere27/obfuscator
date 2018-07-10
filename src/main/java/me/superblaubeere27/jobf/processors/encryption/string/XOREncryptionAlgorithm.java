package me.superblaubeere27.jobf.processors.encryption.string;

import java.io.UnsupportedEncodingException;
import java.util.Base64;

public class XOREncryptionAlgorithm implements IStringEncryptionAlgorithm {

    public static String decrypt(String obj, String key) {
        try {
            obj = new String(Base64.getDecoder().decode(obj.getBytes("UTF-8")), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        StringBuilder sb = new StringBuilder();
        char[] keyChars = key.toCharArray();
        int i = 0;
        for (char c : obj.toCharArray()) {
            sb.append((char) (c ^ keyChars[i % keyChars.length]));
            i++;
        }
        return sb.toString();
    }

    @Override
    public String encrypt(String obj, String key) {
        StringBuilder sb = new StringBuilder();
        char[] keyChars = key.toCharArray();
        int i = 0;
        for (char c : obj.toCharArray()) {
            sb.append((char) (c ^ keyChars[i % keyChars.length]));
            i++;
        }
        try {
            return new String(Base64.getEncoder().encode(sb.toString().getBytes("UTF-8")), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
    }
}
