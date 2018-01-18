package com.wayne.www.waynelib.fdc.message;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Think on 4/19/2016.
 */
public class MD5Crypter {
    public static String passphrase = "DresserFusion}123";

    public byte[] getPassphrase() {
        return MD5Crypter.passphrase.getBytes();
    }

    public String getMD5Hash(String data) throws NoSuchAlgorithmException {
        return this.getMD5Hash(data.getBytes(StandardCharsets.US_ASCII));
    }

    public String getMD5Hash(byte[] data) throws NoSuchAlgorithmException {
        MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
        digest.update(data);
        byte[] md5HashedBytes = digest.digest();
        String md5HashedString = new String(md5HashedBytes, StandardCharsets.US_ASCII);
        return md5HashedString;
    }

    public byte[] computeHash(byte[] data) throws NoSuchAlgorithmException {
        MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
        byte[] md5HashedBytes = digest.digest(data);
        return md5HashedBytes;
    }

    public Boolean verifyMD5Hash(String input, String hash) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        // Hash the input.
        String hashOfInput = getMD5Hash(input);
        if (hashOfInput.equalsIgnoreCase(hash)) {
            return true;
        } else {
            return false;
        }
    }
}
