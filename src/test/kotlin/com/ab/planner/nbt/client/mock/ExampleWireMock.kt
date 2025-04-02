package com.ab.planner.nbt.client.mock

import com.ab.planner.nbt.client.factory.simpleRouteResponse
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.tomakehurst.wiremock.client.WireMock.aResponse
import com.github.tomakehurst.wiremock.client.WireMock.equalToJson
import com.github.tomakehurst.wiremock.client.WireMock.post
import com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo
import com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig
import com.github.tomakehurst.wiremock.junit.WireMockRule
import io.quarkus.logging.Log
import io.quarkus.test.common.QuarkusTestResourceLifecycleManager

private const val QUARKUS_REST_CLIENT_EXAMPLE_API_URL = "quarkus.rest-client.example-api.url"

class ExampleWireMock : QuarkusTestResourceLifecycleManager {
    private val server: WireMockRule =
        WireMockRule(
            wireMockConfig()
                .dynamicPort()
                .usingFilesUnderDirectory("target"),
        )

    private val objectMapper: ObjectMapper = ObjectMapper()

    override fun start(): Map<String, String> {
        server.start()

        Log.info("Started Example WireMock server on port ${server.port()}")

        server.stubFor(
            post(urlEqualTo("/index.php"))
                .withRequestBody(equalToJson("{ \"type\": \"route\" }", true, true))
                .willReturn(
                    aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody(objectMapper.writeValueAsString(simpleRouteResponse)),
                ),
        )

        Log.info("Started Example WireMock server on port ${server.port()}")
        return mapOf(QUARKUS_REST_CLIENT_EXAMPLE_API_URL to server.baseUrl())
    }

    override fun stop() {
        server.stop()
        System.clearProperty(QUARKUS_REST_CLIENT_EXAMPLE_API_URL)
        Log.info("Stopped Example WireMock server")
    }
}
