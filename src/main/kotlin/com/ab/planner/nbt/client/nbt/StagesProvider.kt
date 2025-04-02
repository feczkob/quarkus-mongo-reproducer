package com.ab.planner.nbt.client.nbt

import com.ab.planner.nbt.client.nbt.mapper.StageMapper
import com.ab.planner.nbt.domain.exception.NotFoundException
import com.ab.planner.nbt.domain.model.nbt.StampingLocation
import io.quarkus.logging.Log
import io.quarkus.runtime.Startup
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Named
import jakarta.ws.rs.Produces
import org.eclipse.microprofile.config.inject.ConfigProperty

private const val STAGES_FILE = "stages.csv"
private const val DELIMITER = ";"
private const val FILE_NOT_FOUND = "File not found!"

@ApplicationScoped
class StagesProvider(
    @ConfigProperty(name = "nbt.files.path")
    val filesPath: String,
    @Named("stampingLocations")
    private val stampingLocations: Map<Long, StampingLocation>,
    private val stageMapper: StageMapper,
) {
    @Produces
    @Named("stages")
    @Startup
    @ApplicationScoped
    fun readInStages(): Map<Long, com.ab.planner.nbt.domain.model.nbt.Stage> {
        Log.info("Reading in stages from $filesPath/$STAGES_FILE")
        val inputStream =
            Thread
                .currentThread()
                .contextClassLoader
                .getResource("$filesPath/$STAGES_FILE")
                ?.openStream()
                ?: throw IllegalStateException("$FILE_NOT_FOUND: $filesPath/$STAGES_FILE")
        val reader = inputStream.bufferedReader()
        reader.readLine()

        return reader
            .readLines()
            .map { line ->
                val columns = line.split(DELIMITER)
                Stage(
                    sectionId = columns[0].toLong(),
                    stageId = columns[1].toLong(),
                    startId = columns[2].toLong(),
                    endId = columns[4].toLong(),
                    length = columns[6],
                    elevationGain = columns[7].toLong(),
                    elevationLoss = columns[8].toLong(),
                    timeFromStartToEnd = columns[9],
                    timeFromEndToStart = columns[10],
                )
            }.map {
                val start =
                    stampingLocations[it.startId]
                        ?: throw NotFoundException("Wrong config! Stamping location not found with id: ${it.startId}")
                val end =
                    stampingLocations[it.endId]
                        ?: throw NotFoundException("Wrong config! Stamping location not found with id: ${it.endId}")
                stageMapper.mapToDomain(it, start, end)
            }.associateBy { it.id }
    }
}
