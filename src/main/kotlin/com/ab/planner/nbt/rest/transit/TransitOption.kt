package com.ab.planner.nbt.rest.transit

import com.ab.planner.nbt.rest.DURATION_REGEX
import org.eclipse.microprofile.openapi.annotations.media.Schema
import java.time.LocalDateTime

// TODO #155 remove later
@Schema(
    name = "TransitOption",
    description = "Class representing a possible option of a transit with public transport",
    requiredProperties = ["departureTime", "arrivalTime", "travelTime", "numberOfChanges", "parts"],
)
data class TransitOption(
    @field:Schema(implementation = LocalDateTime::class)
    val departureTime: LocalDateTime,
    @field:Schema(implementation = LocalDateTime::class)
    val arrivalTime: LocalDateTime,
    @field:Schema(
        description = "Duration of the transit in ISO 8601 format [PThHmM]",
        pattern = DURATION_REGEX,
        example = "PT2H10M",
    )
    val travelTime: String,
    @field:Schema(description = "Number of changes")
    val numberOfChanges: Int,
    @field:Schema(description = "Ticket price")
    val ticketPrice: Int?,
    @field:Schema(description = "Parts of the transit")
    val parts: List<TransitPart>,
)
