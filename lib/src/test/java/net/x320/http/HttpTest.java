package net.x320.http;

import net.x320.dns.HurricaneElectric;
import org.junit.jupiter.api.Test;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.assertj.core.api.Assertions.assertThat;

public class HttpTest {
    String formTag = """
            <form name="login" method="post" enctype="application/x-www-form-urlencoded" action="/">
            """;

    @Test
    void https0() throws Exception {
        var cMgr = new CookieManager();
        CookieHandler.setDefault(cMgr);
        try (var http = HttpClient.newBuilder().cookieHandler(cMgr).build()) {
            var request = HttpRequest.newBuilder(HurricaneElectric.BASE_URL).GET().build() ;
            var response = http.send(request, HttpResponse.BodyHandlers.ofString());
            for (var ck : cMgr.getCookieStore().getCookies()) {
                System.out.printf("Domain: %s; Path: %s%nHttpOnly: %b;  HTTPS-only: %b%n" +
                                "Name: %s \nValue: %s%n",
                        ck.getDomain(), ck.getPath(), ck.isHttpOnly(), ck.getSecure(), ck.getName(), ck.getValue());
            }
            assertThat(response.statusCode()).isEqualTo(200);
            var loginPage = response.body();
            System.out.println("response.body.length " + loginPage.length() + " Characters"); // 16454
            assertThat(loginPage).contains("Free DNS Login");
        }
    }
}
