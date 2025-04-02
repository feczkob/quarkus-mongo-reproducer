package com.ab.planner.nbt.rest

import com.ab.planner.nbt.client.mock.EmbeddedMongoDb
import com.ab.planner.nbt.client.mock.ExampleWireMock
import io.quarkus.test.junit.QuarkusTestProfile

class DefaultTestProfile : QuarkusTestProfile {
    override fun testResources(): List<QuarkusTestProfile.TestResourceEntry> =
        listOf(
            QuarkusTestProfile.TestResourceEntry(
                ExampleWireMock::class.java,
                emptyMap(),
                true,
            ),
            QuarkusTestProfile.TestResourceEntry(
                EmbeddedMongoDb::class.java,
                emptyMap(),
                true,
            ),
        )
}

