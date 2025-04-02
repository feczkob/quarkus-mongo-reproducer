package com.ab.planner.nbt.client.nbt

import com.ab.planner.nbt.client.nbt.mapper.StampingLocationMapper
import io.quarkus.logging.Log
import io.quarkus.runtime.Startup
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Named
import jakarta.ws.rs.Produces
import org.eclipse.microprofile.config.inject.ConfigProperty

private const val STAMPING_LOCATIONS_FILE = "stamping-locations.csv"
private const val DELIMITER = ";"
private const val FILE_NOT_FOUND = "File not found!"

@ApplicationScoped
class StampingLocationsProvider(
    private val stampingLocationMapper: StampingLocationMapper,
    @ConfigProperty(name = "nbt.files.path")
    val filesPath: String,
) {
    @Produces
    @Named("stampingLocations")
    @Startup
    @ApplicationScoped
    fun readInStampingLocations(): Map<Long, com.ab.planner.nbt.domain.model.nbt.StampingLocation> {
        Log.info("Reading in stamping locations from $filesPath/$STAMPING_LOCATIONS_FILE")
        val inputStream =
            Thread
                .currentThread()
                .contextClassLoader
                .getResource("$filesPath/$STAMPING_LOCATIONS_FILE")
                ?.openStream()
                ?: throw IllegalStateException("$FILE_NOT_FOUND: $filesPath/$STAMPING_LOCATIONS_FILE")
        val reader = inputStream.bufferedReader()
        reader.readLine()

        return reader
            .readLines()
            .map { line ->
                val columns = line.split(DELIMITER)
                StampingLocation(
                    stampingLocationId = columns[0].toLong(),
                    stampingLocation = columns[1],
                    settlementId = columns[2].toLongOrNull(),
                    stopId = columns[3].toLongOrNull(),
                )
            }.map { stampingLocationMapper.mapToDomain(it) }
            .associateBy { it.id }
    }
}
