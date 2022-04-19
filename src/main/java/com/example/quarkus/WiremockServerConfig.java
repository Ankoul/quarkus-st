package com.example.quarkus;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;

import com.github.tomakehurst.wiremock.WireMockServer;
import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;

@ApplicationScoped
public class WiremockServerConfig {

    private WireMockServer wireMockServer;

    void onStart(@Observes StartupEvent ev) {
        wireMockServer = new WireMockServer(
                options()
                .usingFilesUnderClasspath("wiremock/author-service")
                .port(8000)
        );
        wireMockServer.start();
    }

    void onStop(@Observes ShutdownEvent ev) {
        wireMockServer.stop();
    }
}
