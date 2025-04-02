package com.ab.planner.nbt.rest.transit

import org.eclipse.microprofile.openapi.annotations.media.Schema
import java.time.LocalDate

// TODO #155 remove later
@Schema(
    name = "Transit",
    description =
        "Class representing a transit of public transport between two stations. " +
            "We use \"transit\" and \"travel\" interchangeably, however the former expresses more clearly that we're talking about replacement via public transport.",
    requiredProperties = ["travelDate", "from", "to"],
)
data class Transit(
    @field:Schema(implementation = LocalDate::class)
    val travelDate: LocalDate,
    @field:Schema(implementation = Settlement::class)
    val from: Settlement,
    @field:Schema(implementation = Settlement::class)
    val to: Settlement,
)

