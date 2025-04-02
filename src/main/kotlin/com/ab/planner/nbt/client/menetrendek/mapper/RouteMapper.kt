package com.ab.planner.nbt.client.menetrendek.mapper

import com.ab.planner.nbt.domain.model.travel.Stop
import com.ab.planner.nbt.domain.model.travel.Transit
import com.ab.planner.nbt.domain.model.travel.TransitMode
import com.ab.planner.nbt.domain.model.travel.TransitOption
import com.ab.planner.nbt.domain.model.travel.TransitPart
import com.ab.planner.nbt.domain.plus
import com.example.model.Route
import com.example.model.RouteNativeData
import com.example.model.RouteRequest
import com.example.model.RouteRequestParams
import com.example.model.RouteResult
import io.quarkus.logging.Log
import jakarta.enterprise.context.ApplicationScoped
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import kotlin.time.Duration.Companion.minutes

private const val DEFAULT_HOUR = "0"
private const val DEFAULT_MIN = "0"
private const val UNKNOWN_TICKET_PRICE = -1

@ApplicationScoped
class RouteMapper {
    fun mapToApi(
        from: Stop,
        to: Stop,
        travelDate: LocalDate,
    ): RouteRequest =
        RouteRequest(
            type = "route",
            params =
                RouteRequestParams(
                    datum = travelDate,
                    honnanLsId = from.stopId.toInt(),
                    honnanSettlementId = from.settlementId.toInt(),
                    hovaLsId = to.stopId.toInt(),
                    hovaSettlementId = to.settlementId.toInt(),
                    hour = DEFAULT_HOUR,
                    min = DEFAULT_MIN,
                ),
        )

    fun mapToDomain(routeResult: RouteResult): Transit {
        val date = LocalDate.parse(routeResult.dateGot)
        return Transit(
            date = date,
            options =
                routeResult.talalatok?.values?.map { route ->
                    return@map constructTransitOption(route, date)
                } ?: emptyList(),
        )
    }

    private fun constructTransitOption(
        route: Route,
        date: LocalDate,
    ) = TransitOption(
        ticketPrice = if (route.totalFare == UNKNOWN_TICKET_PRICE) null else route.totalFare,
        parts = route.nativeData.map { mapRoutePart(it, date) },
    )

    private fun mapRoutePart(
        part: RouteNativeData,
        date: LocalDate,
    ): TransitPart =
        TransitPart(
            from =
                Stop(
                    settlementId = part.departureSettle.toString(),
                    settlementName = part.fromSettle,
                    stopId = part.departureStation.toString(),
                    stopName = part.depStationName,
                ),
            to =
                Stop(
                    settlementId = part.arrivalSettle.toString(),
                    settlementName = part.toSettle,
                    stopId = part.arrivalStation.toString(),
                    stopName = part.arrStationName,
                ),
            departureTime = LocalDateTime.of(date, LocalTime.of(0, 0)) + part.departureTime.toLong().minutes,
            arrivalTime = LocalDateTime.of(date, LocalTime.of(0, 0)) + part.arrivalTime.toLong().minutes,
            mode = mapTransportMode(part),
            transitNumber = part.longName,
        )

    private fun mapTransportMode(part: RouteNativeData) =
        when (part.transportMode) {
            "vehicles.train", "vehicles.localTrain" -> TransitMode.TRAIN
            "vehicles.bus", "vehicles.localBus", "vehicles.agglo" -> TransitMode.BUS
            "vehicles.ship" -> TransitMode.SHIP
            "vehicles.metro" -> TransitMode.METRO
            "vehicles.tram" -> TransitMode.TRAM
            else -> {
                Log.warn(
                    "Unsupported transport mode: \"${part.transportMode}\" " +
                        "between ${part.fromSettle} and ${part.toSettle}",
                )
                TransitMode.UNKNOWN
            }
        }
}
