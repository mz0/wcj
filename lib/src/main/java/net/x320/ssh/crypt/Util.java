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

    // TODO read from Agent: https://github.com/apache/mina-sshd/blob/master/sshd-core/src/main/java/
    //  org/apache/sshd/agent/unix/AgentClient.java
    //   For Windows see  https://stackoverflow.com/questions/12452933/putty-pageant-protocol
    //    http://api.libssh.org/rfc/PROTOCOL.agent
    //    https://github.com/ymnk/jsch-agent-proxy/blob/master/jsch-agent-proxy-pageant/src/main/java/
    //  com/jcraft/jsch/agentproxy/connector/PageantConnector.java
    //   See also
    //  https://interworks.com/blog/2021/09/15/setting-up-ssh-agent-in-windows-for-passwordless-git-authentication/
    //   PS> Get-Service ssh-agent | Set-Service -StartupType Automatic -PassThru | Start-Service
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
