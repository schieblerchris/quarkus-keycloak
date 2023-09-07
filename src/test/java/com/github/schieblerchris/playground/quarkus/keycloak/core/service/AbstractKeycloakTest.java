package com.github.schieblerchris.playground.quarkus.keycloak.core.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.logging.Log;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public abstract class AbstractKeycloakTest {

    public static final String KEYCLOAK_URL_TEMPLATE = "http://localhost:$port/realms/$realm/protocol/openid-connect/token";

     public static final String CURL_TEMPLATE = """
            curl -v \\
             -d '$dataString' \\
             -H 'Content-Type: application/x-www-form-urlencoded' \\
             -X POST \\
             $url | \\
             jq '.access_token'
            """;

    @ConfigProperty(name = "quarkus.keycloak.devservices.port")
    String port;
    @ConfigProperty(name = "realm-name")
    String realm;
    @ConfigProperty(name = "quarkus.oidc.client-id")
    String client;
    @ConfigProperty(name = "quarkus.oidc.credentials.secret")
    String clientSecret;

    @Inject
    ObjectMapper objectMapper;

    String dataString;

    @BeforeEach
    void beforeEach() {
        Log.infov("using keycloak on port {0} with realm {1}, client {2} and secret {3}", port, realm, client, clientSecret);

        dataString = createDataString();

        var curlProcessed = CURL_TEMPLATE
                .replace("$url", KEYCLOAK_URL_TEMPLATE)
                .replace("$port", port)
                .replace("$realm", realm)
                .replace("$dataString", dataString);
        Log.infov("curl: \n{0}", curlProcessed);

    }

    protected abstract String createDataString();

    @Test
    void testClientLogin() throws Exception {
        var urlString = KEYCLOAK_URL_TEMPLATE
                .replace("$url", KEYCLOAK_URL_TEMPLATE)
                .replace("$port", port)
                .replace("$realm", realm);

        var url = new URL(urlString);
        var con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        con.setRequestProperty("Accept", "application/json");
        con.setDoOutput(true);
        try (OutputStream os = con.getOutputStream()) {
            var input = dataString.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }
        var response = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8))) {
            var responseLine = "";
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
        }
        Log.infov("received: {0}", response);
        var accessTokenString = objectMapper.readTree(response.toString()).get("access_token").asText();

        var accessTokenParts = accessTokenString.split("\\.");
        var header = decodeTokenPart(accessTokenParts[0]);
        var payload = decodeTokenPart(accessTokenParts[1]);
        Log.infov("header: \n{0}", objectMapper.readTree(header).toPrettyString());
        Log.infov("payload: \n{0}", objectMapper.readTree(payload).toPrettyString());
        con.disconnect();
    }

    private static String decodeTokenPart(String tokenPart) {
        return new String(Base64.getDecoder().decode(tokenPart), StandardCharsets.US_ASCII);
    }
}
