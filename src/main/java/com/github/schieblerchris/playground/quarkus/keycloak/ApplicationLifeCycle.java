package com.github.schieblerchris.playground.quarkus.keycloak;

import io.quarkus.logging.Log;
import io.quarkus.runtime.StartupEvent;
import io.quarkus.runtime.configuration.ConfigUtils;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;

@ApplicationScoped
public class ApplicationLifeCycle {

    void onStart(@Observes StartupEvent ev) {
        Log.infov("The application is starting with profile {0}", ConfigUtils.getProfiles());
    }
}
