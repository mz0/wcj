package net.x320.ssh.crypt;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.security.InvalidKeyException;
import java.security.InvalidParameterException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.util.Arrays;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// 2019-02-08 by Taras (https://stackoverflow.com/users/769282/taras)
/** 2014-06-21 <a href="https://stackoverflow.com/a/24343938/228117">stackoverflow.com/a/24343938/228117</a> */
public class KeyUtils {
    private static final Pattern SSH_RSA_PATTERN = Pattern.compile("ssh-rsa\\s+([A-Za-z0-9/+]+=*)\\s+.*");
    private static final int UINT32_BYTES = 4;
    private static final byte[] INITIAL_PREFIX = new byte[]{0x00, 0x00, 0x00, 0x07,
            0x73, 0x73, 0x68, 0x2d, 0x72, 0x73, 0x61};
    /* RSA public key format  // TODO decouple "ssh-rsa" check
      00 00 00 07             The length in bytes of the next field
      73 73 68 2d 72 73 61    The key type (ASCII encoding of "ssh-rsa")
      00 00 00 03             The length in bytes of the public Exponent
      01 00 01                The public Exponent (usually 65537, as here)
      00 00 01 01             The length in bytes of the Modulus (here, 257)
      00 c3 a3...             The Modulus
    */
    public static RSAPublicKey parseSSHPublicKey(String key) throws InvalidKeyException {
        Matcher matcher = SSH_RSA_PATTERN.matcher(key.trim());
        if (!matcher.matches()) {
            throw new InvalidKeyException("Key format is invalid for SSH RSA.");
        }
        String keyStr = matcher.group(1);
        ByteArrayInputStream is = new ByteArrayInputStream(Base64.getDecoder().decode(keyStr));
        byte[] prefix = new byte[INITIAL_PREFIX.length];
        try {
            if (INITIAL_PREFIX.length != is.read(prefix) || !Arrays.equals(INITIAL_PREFIX, prefix)) {
                throw new InvalidKeyException("'ssh-rsa' key prefix not found");
            }
            BigInteger exponent = getValue(is);
            BigInteger modulus = getValue(is);
            return (RSAPublicKey) KeyFactory.getInstance("RSA").generatePublic(new RSAPublicKeySpec(modulus, exponent));
        } catch (IOException | InvalidKeySpecException | NoSuchAlgorithmException e) {
            throw new InvalidKeyException("Failed to read SSH RSA certificate from string", e);
        }
    }

    private static BigInteger getValue(InputStream is) throws IOException {
        byte[] lenBuff = new byte[UINT32_BYTES];
        if (UINT32_BYTES != is.read(lenBuff)) {
            throw new InvalidParameterException("Unable to read first 4 bytes.");
        }
        int len = ByteBuffer.wrap(lenBuff).getInt();
        byte[] valueArray = new byte[len];
        if (len != is.read(valueArray)) {
            throw new InvalidParameterException(String.format("Unable to read %d bytes.", len));
        }
        return new BigInteger(valueArray);
    }
}