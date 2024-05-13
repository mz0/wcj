package net.x320.wcj;

import net.x320.ssh.crypt.Util;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;

import static org.assertj.core.api.Assertions.assertThat;

import java.security.interfaces.RSAPublicKey;
class UtilTest {
    @Test
    void readSshRsaPubKeyCheck() {
        var propHome = System.getProperty("user.home");
        var envHome = homeDir();
        assertThat(propHome)
                .as("%s == %s", "System.getProperty(\"user.home\")", "getEnv(\"HOME\")")
                .isEqualTo(envHome);
        PublicKey pubKey = null;
        try {
            pubKey = Util.readSshRsaPublicKey(idRsaPubPath());
        } catch (NoSuchAlgorithmException | IOException | InvalidKeySpecException | InvalidKeyException e) {
            // java.security.InvalidKeyException: Unable to decode key
            System.out.println(e.getMessage());
            printEnv();
            printProperties();
        }
        assertThat(pubKey).isNotNull();
        System.out.printf("Public key algorithm: %s%n", pubKey.getAlgorithm());
        assertThat(pubKey).isInstanceOf(RSAPublicKey.class);
        var pubK = (RSAPublicKey) pubKey;
        System.out.printf("Modulus %d-bit%nExponent %d%n", pubK.getModulus().bitLength(), pubK.getPublicExponent());

    }

    private String homeDir() {
        return System.getenv("HOME");
    }

    private Path idRsaPubPath() {
        return Path.of(homeDir(),".ssh", "id_rsa.pub");
    }

    /** print e.g. USER = mz0; HOME = /home/mz0; PWD = /home/mz0/p/web-crawl-java/app; */
    private void printEnv() {
        System.getenv().forEach((k, v) -> {
            if (k.startsWith("US") || k.startsWith("HO") || k.startsWith("PW"))
                System.out.println(k + " = " + v);
        });
    }

    private void printProperties() {
        // java.runtime.version, java.vm.version: 21.0.2+13-Ubuntu-120.04.1
        // java.library.path : /usr/java/packages/lib:/usr/lib/x86_64-linux-gnu/jni:/lib/x86_64-linux-gnu
        //   :/usr/lib/x86_64-linux-gnu:/usr/lib/jni:/lib:/usr/lib
        // user.dir : /home/mz0/p/web-crawl-java/app
        System.getProperties().forEach((k,v) -> {
            System.out.println(k + " : " + v); // os.name: Linux; file.separator: /; user.name: mz0
        });
    }
}
