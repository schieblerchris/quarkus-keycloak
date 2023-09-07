package com.github.schieblerchris.playground.quarkus.keycloak.core.service;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.QuarkusTestProfile;
import io.quarkus.test.junit.TestProfile;

@QuarkusTest
@TestProfile(KeycloakBackendClientTest.BackendProfile.class)
class KeycloakBackendClientTest extends AbstractKeycloakTest {

    public static class BackendProfile implements QuarkusTestProfile {
        @Override
        public String getConfigProfile() {
            return "backend";
        }
    }

    public static final String DATA_TEMPLATE = "client_id=$client_id&username=$username&password=$password&grant_type=password&client_secret=$client_secret";

    @Override
    protected String createDataString() {
        return DATA_TEMPLATE
                .replace("$client_id", client)
                .replace("$client_secret", clientSecret)
                .replace("$username", "andariel")
                .replace("$password", "password");
    }
}
