package com.ab.planner.nbt.rest.plan

import com.ab.planner.nbt.rest.DURATION_REGEX
import org.eclipse.microprofile.openapi.annotations.media.Schema

// TODO #155 remove later
@Schema(
    name = "HikeSummary",
    description = "Class representing the summary of a hike",
    requiredProperties = ["totalTime", "totalDistance", "totalElevationGain", "totalElevationLoss"],
)
data class HikeSummary(
    @field:Schema(
        description = "Total time of the hike in ISO 8601 format [PThHmM]",
        pattern = DURATION_REGEX,
        example = "PT2H10M",
    )
    val totalTime: String,
    @field:Schema(description = "Total distance of the hike [km]")
    val totalDistance: Double,
    @field:Schema(description = "Total elevation gain of the hike [m]")
    val totalElevationGain: Long,
    @field:Schema(description = "Total elevation loss of the hike [m]")
    val totalElevationLoss: Long,
)
