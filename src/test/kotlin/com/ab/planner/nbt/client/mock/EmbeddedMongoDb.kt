package com.ab.planner.nbt.client.mock

import de.flapdoodle.embed.mongo.distribution.Version
import de.flapdoodle.embed.mongo.transitions.Mongod
import de.flapdoodle.embed.mongo.transitions.RunningMongodProcess
import de.flapdoodle.reverse.StateID
import de.flapdoodle.reverse.TransitionWalker
import de.flapdoodle.reverse.Transitions
import io.quarkus.logging.Log
import io.quarkus.test.common.QuarkusTestResourceLifecycleManager

class EmbeddedMongoDb : QuarkusTestResourceLifecycleManager {
    private val transitions: Transitions = Mongod.instance().transitions(Version.Main.V7_0)
    private lateinit var running: TransitionWalker.ReachedState<RunningMongodProcess>

    override fun start(): Map<String, String> {
        running = transitions.walker().initState(StateID.of(RunningMongodProcess::class.java))
        val serverAddress = running.current().serverAddress
        Log.info("Started MongoDB on port: ${serverAddress.port}")

        // * https://github.com/javiertoja/stackoverflow/tree/main/kotlin-integration-tests-mongo
        return mapOf("quarkus.mongodb.connection-string" to "mongodb://$serverAddress/nbtPlannerDB")
    }

    override fun stop() {
        running.close()
        Log.info("Stopped MongoDB container")
    }
}
