package com.cmpeq0.controlsystem.utils;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class SecretKeyManager {

    public static String encrypt(String data, String key) {
        String result = "key";
        try {
            SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] encryptedBytes = cipher.doFinal(data.getBytes());
            result = Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            //ignore
        }
        return result;

    }

    // Метод для дешифрования строки
    public static String decrypt(String encryptedData, String key) {
        String result = "key";
        try {
            SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedData));
            result =  new String(decryptedBytes);
        } catch (Exception e) {
            //ignore
        }
        return result;
    }

    public static String generateKey() {
        try {
            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(128); // Для AES-128
            SecretKey secretKey = keyGen.generateKey();
            return Base64.getEncoder().encodeToString(secretKey.getEncoded());
        } catch (Exception e) {
            return "key";
        }
    }

}
