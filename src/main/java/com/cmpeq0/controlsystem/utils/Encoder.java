package com.cmpeq0.controlsystem.utils;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Component;

public class Encoder {

    public static String encrypt(String data) {
        return DigestUtils.sha256Hex(data);
    }

    public static boolean matchSHA(String data, String original) {
        return original.equals(encrypt(data));
    }

}
