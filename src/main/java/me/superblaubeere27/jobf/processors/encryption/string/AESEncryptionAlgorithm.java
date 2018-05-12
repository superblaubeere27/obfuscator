package me.superblaubeere27.jobf.processors.encryption.string;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;
import java.util.Base64;

public class AESEncryptionAlgorithm implements IStringEncryptionAlgorithm {

    public static String decrypt(String obj, String key) {
        try {
            SecretKeySpec keySpec = new SecretKeySpec(MessageDigest.getInstance("SHA-256").digest(key.getBytes("UTF-8")), "AES");

            Cipher des = Cipher.getInstance("AES");
            des.init(Cipher.DECRYPT_MODE, keySpec);

            return new String(des.doFinal(Base64.getDecoder().decode(obj.getBytes("UTF-8"))), "UTF-8");

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String encrypt(String obj, String key) {
        try {
            SecretKeySpec keySpec = new SecretKeySpec(MessageDigest.getInstance("SHA-256").digest(key.getBytes("UTF-8")), "AES");

            Cipher des = Cipher.getInstance("AES");
            des.init(Cipher.ENCRYPT_MODE, keySpec);

            return new String(Base64.getEncoder().encode(des.doFinal(obj.getBytes("UTF-8"))), "UTF-8");

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
