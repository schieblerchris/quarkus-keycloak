package com.github.schieblerchris.playground.quarkus.keycloak;

import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.annotations.QuarkusMain;

import java.util.Locale;
import java.util.TimeZone;

@QuarkusMain
public class ServerMain {

    public static void main(String... args) {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        Locale.setDefault(Locale.UK);
        Quarkus.run(args);
    }

}
