package com.ab.planner.nbt.client.menetrendek.mapper

import com.ab.planner.nbt.client.menetrendek.MongoStop
import com.ab.planner.nbt.client.menetrendek.MongoTransit
import com.ab.planner.nbt.client.menetrendek.MongoTransitOption
import com.ab.planner.nbt.client.menetrendek.MongoTransitPart
import com.ab.planner.nbt.domain.model.travel.Stop
import com.ab.planner.nbt.domain.model.travel.Transit
import com.ab.planner.nbt.domain.model.travel.TransitMode
import com.ab.planner.nbt.domain.model.travel.TransitOption
import com.ab.planner.nbt.domain.model.travel.TransitPart
import jakarta.enterprise.context.ApplicationScoped
import java.time.LocalDateTime

@ApplicationScoped
class MongoTransitMapper {
    fun mapToEntity(
        from: MongoStop,
        to: MongoStop,
        transit: Transit,
    ): MongoTransit =
        MongoTransit().apply {
            this.from = from
            this.to = to
            date = transit.date
            options = transit.options.map { mapToEntity(it) }
        }

    private fun mapToEntity(option: TransitOption) =
        MongoTransitOption().apply {
            ticketPrice = option.ticketPrice
            parts = option.parts.map { mapToEntity(it) }
        }

    private fun mapToEntity(part: TransitPart) =
        MongoTransitPart().apply {
            from = mapToEntity(part.from)
            to = mapToEntity(part.to)
            departureTime = part.departureTime.toString()
            arrivalTime = part.arrivalTime.toString()
            mode = part.mode.toString()
            transitNumber = part.transitNumber
        }

    fun mapToEntity(stop: Stop) =
        MongoStop().apply {
            settlementId = stop.settlementId
            settlementName = stop.settlementName
            stopId = stop.stopId
            stopName = stop.stopName
        }

    fun mapToDomain(transit: MongoTransit): Transit =
        Transit(
            date = transit.date,
            options = transit.options.map { mapToDomain(it) },
        )

    fun mapToDomain(option: MongoTransitOption): TransitOption =
        TransitOption(
            ticketPrice = option.ticketPrice,
            parts = option.parts.map { mapToDomain(it) },
        )

    fun mapToDomain(part: MongoTransitPart): TransitPart =
        TransitPart(
            from = mapToDomain(part.from),
            to = mapToDomain(part.to),
            departureTime = LocalDateTime.parse(part.departureTime),
            arrivalTime = LocalDateTime.parse(part.arrivalTime),
            mode = TransitMode.valueOf(part.mode),
            transitNumber = part.transitNumber,
        )

    fun mapToDomain(stop: MongoStop): Stop =
        Stop(
            settlementId = stop.settlementId,
            settlementName = stop.settlementName,
            stopId = stop.stopId,
            stopName = stop.stopName,
        )
}
