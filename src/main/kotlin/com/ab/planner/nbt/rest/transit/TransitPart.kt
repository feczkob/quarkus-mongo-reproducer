package com.ab.planner.nbt.rest.transit

import org.eclipse.microprofile.openapi.annotations.media.Schema
import java.time.LocalDateTime

// TODO #155 remove later
@Schema(
    name = "TransitPart",
    description = "Class representing a part of a transit with public transport",
    requiredProperties = ["from", "to", "departureTime", "arrivalTime", "mode", "transitNumber"],
)
data class TransitPart(
    @field:Schema(description = "Departure station")
    val from: String,
    @field:Schema(description = "Arrival station")
    val to: String,
    @field:Schema(implementation = LocalDateTime::class)
    val departureTime: LocalDateTime,
    @field:Schema(implementation = LocalDateTime::class)
    val arrivalTime: LocalDateTime,
    @field:Schema(description = "Mode of transport. Possible values: TRAIN, BUS, SHIP, METRO, TRAM, UNKNOWN")
    val mode: String,
    @field:Schema(description = "Transit number")
    val transitNumber: String,
)
