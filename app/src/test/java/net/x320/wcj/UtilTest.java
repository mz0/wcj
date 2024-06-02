package net.x320.wcj;

import net.x320.ssh.crypt.Util;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;

import static org.assertj.core.api.Assertions.assertThat;

class UtilTest {
    @Test
    void readSshRsaPubKeyCheck() {
        PublicKey pubKey = null;
        try {
            pubKey = Util.readSshRsaPublicKey(idRsaPubTestPath());
        } catch (NoSuchAlgorithmException | IOException | InvalidKeySpecException | InvalidKeyException e) {
            // java.security.InvalidKeyException: Unable to decode key
            System.out.println(e.getMessage());
        } catch (URISyntaxException e) {
            System.out.println(e.getMessage());
        }
        assertThat(pubKey).isNotNull();
        System.out.printf("Public key algorithm: %s%n", pubKey.getAlgorithm());
        assertThat(pubKey).isInstanceOf(RSAPublicKey.class);
        var pubK = (RSAPublicKey) pubKey;
        System.out.printf("Modulus %d-bit%nExponent %d%n", pubK.getModulus().bitLength(), pubK.getPublicExponent());
    }

    private Path idRsaPubTestPath() throws URISyntaxException {
        URL dotSsh = this.getClass().getClassLoader().getResource(".ssh");
        assertThat(dotSsh).as("resource .ssh is found").isNotNull();
        return Paths.get(dotSsh.toURI()).resolve("id_rsa.pub");
    }
}
