package net.x320.wcj;

import net.x320.ssh.crypt.Util;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import static org.assertj.core.api.Assertions.assertThat;


class UtilTest {
    @Test
    void testGetMessage() {
        try {
            Util.readRsaPublicKeyDer("/home/mz0/.ssh/id_rsa_.pub");
        } catch (NoSuchAlgorithmException | IOException | InvalidKeySpecException e) {
            // java.security.InvalidKeyException: Unable to decode key for (id_rsa is password protected)
            System.out.println(e.getMessage());
        }
        System.getenv().forEach((k, v) -> {
            if (k.startsWith("US") || k.startsWith("HO") || k.startsWith("PW"))
                System.out.println(k + " = " + v);
        });
        assertThat("A").isEqualToIgnoringCase("a");
        // USER = mz0; HOME = /home/mz0; PWD = /home/mz0/p/web-crawl-java/app;
        System.out.println("user.home : " + System.getProperty("user.home"));
        // java.runtime.version, java.vm.version: 21.0.2+13-Ubuntu-120.04.1
        // java.library.path : /usr/java/packages/lib:/usr/lib/x86_64-linux-gnu/jni:/lib/x86_64-linux-gnu
        //   :/usr/lib/x86_64-linux-gnu:/usr/lib/jni:/lib:/usr/lib
        // user.dir : /home/mz0/p/web-crawl-java/app
        System.getProperties().forEach((k,v) -> {
            System.out.println(k + " : " + v); // os.name: Linux; file.separator: /; user.name: mz0
        });
    }
}
