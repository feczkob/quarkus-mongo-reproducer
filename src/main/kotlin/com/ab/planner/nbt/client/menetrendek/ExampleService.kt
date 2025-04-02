package com.ab.planner.nbt.client.menetrendek

import com.ab.planner.nbt.client.menetrendek.mapper.MongoTransitMapper
import com.ab.planner.nbt.client.menetrendek.mapper.RouteMapper
import com.ab.planner.nbt.client.menetrendek.mapper.StationMapper
import com.ab.planner.nbt.domain.exception.ConversionException
import com.ab.planner.nbt.domain.handler.ExampleHandler
import com.ab.planner.nbt.domain.model.travel.Stop
import com.ab.planner.nbt.domain.model.travel.Transit
import com.example.model.RouteResponse
import com.example.model.StationResponse
import com.fasterxml.jackson.databind.ObjectMapper
import io.quarkus.logging.Log
import jakarta.enterprise.context.ApplicationScoped
import org.eclipse.microprofile.rest.client.inject.RestClient
import java.time.LocalDate
import kotlin.time.measureTime

private const val SUCCESS_STATUS = "success"

@ApplicationScoped
class ExampleService(
    private val objectMapper: ObjectMapper,
    private val routeMapper: RouteMapper,
    private val stationMapper: StationMapper,
    private val mongoTransitMapper: MongoTransitMapper,
    @RestClient private val client: ExampleClient,
    private val transitRepository: TransitRepository,
) : ExampleHandler {
    override suspend fun getStops(stopName: String): List<Stop> {
        val request = stationMapper.mapToApi(stopName)

        val baseResponse: Any
        val timeTaken =
            measureTime { baseResponse = client.generalRequest(request) }
        Log.debug("Station request took: $timeTaken")

        val stationResponse =
            try {
                objectMapper.convertValue(baseResponse, StationResponse::class.java)
            } catch (e: Exception) {
                Log.error("Error converting response: $baseResponse", e)
                throw ConversionException("Error converting response to StationResponse")
            }

        return stationResponse.results.map { stationMapper.mapToDomainStop(it) }
    }

    override suspend fun getTransit(
        from: Stop,
        to: Stop,
        travelDate: LocalDate,
    ): Transit {
        check(from.exists() && to.exists()) { "Illegal state: from and to must not be null" }
        check(from != to) { "Illegal state: from and to must not be the same" }

        val fromEntity = mongoTransitMapper.mapToEntity(from)
        val toEntity = mongoTransitMapper.mapToEntity(to)
        val mongoTransit = transitRepository.findTransit(fromEntity, toEntity, travelDate)

        if (mongoTransit != null) {
            return mongoTransitMapper.mapToDomain(mongoTransit)
        }

        val request = routeMapper.mapToApi(from, to, travelDate)

        val baseResponse: Any
        val timeTaken =
            try {
                measureTime { baseResponse = client.generalRequest(request) }
            } catch (e: Exception) {
                Log.warn(
                    "Some error happened during the request with from $from, to $to and travelDate $travelDate, but we don't stop! Returning empty Transit",
                    e,
                )
                return Transit.empty(date = travelDate)
            }
        Log.debug("Route request took: $timeTaken")

        val routeResponse =
            try {
                objectMapper.convertValue(baseResponse, RouteResponse::class.java)
            } catch (e: Exception) {
                Log.error("Error converting response: $baseResponse", e)
                throw ConversionException("Error converting response to RouteResponse")
            }

        val result =
            when (routeResponse.status) {
                SUCCESS_STATUS -> routeMapper.mapToDomain(routeResponse.results)
                else -> {
                    Log.warn("Error status in response, returning empty Transit for: from $from, to $to and travelDate $travelDate")
                    Transit.empty(date = travelDate)
                }
            }
        transitRepository.persist(mongoTransitMapper.mapToEntity(fromEntity, toEntity, result))

        return result
    }
}
