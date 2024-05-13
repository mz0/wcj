/*
 * Copyright (c) 2024 Mark Žitomirski <mz0@outlook.com>
 * This file is a part of WCJ toy project.
 * See LICENSE file in the project root for terms of use and/or distribution.
 */
package net.x320.ssh.crypt;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;

/**
 * 2014-06-21 <a href="https://stackoverflow.com/a/24343938/228117">stackoverflow.com/a/24343938/228117</a>
 * OAEP padding
 */
public class Util {

    public static byte[] decryptWith(PrivateKey key, byte[] ciphertext) throws NoSuchAlgorithmException,
            NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException
    {
        Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA1AndMGF1Padding");
        cipher.init(Cipher.DECRYPT_MODE, key);
        return cipher.doFinal(ciphertext);
    }

    public byte[] encryptWith(PublicKey key, byte[] plaintext) throws NoSuchAlgorithmException,
            NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException
    {
        Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA1AndMGF1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return cipher.doFinal(plaintext);
    }

    public static PrivateKey readPrivateKey(String filename) throws NoSuchAlgorithmException,
            IOException, InvalidKeySpecException
    {
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(readFileBytes(filename));
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePrivate(keySpec);
    }

    public static PublicKey readSshRsaPublicKey(Path file) throws NoSuchAlgorithmException,
            IOException, InvalidKeySpecException, InvalidKeyException {
        return KeyUtils.parseSSHPublicKey(Files.readString(file));
    }


    private static byte[] readFileBytes(String filename) throws IOException {
        return Files.readAllBytes(Paths.get(filename));
    }
}
