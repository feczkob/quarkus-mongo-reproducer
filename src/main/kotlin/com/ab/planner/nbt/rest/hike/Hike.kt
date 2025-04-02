package com.ab.planner.nbt.rest.hike

import com.ab.planner.nbt.rest.nbt.StampingLocation
import com.ab.planner.nbt.rest.plan.HikeSummary
import org.eclipse.microprofile.openapi.annotations.media.Schema
import java.util.UUID

// TODO #155: remove
@Schema(
    name = "Hike",
    description = "Class representing a hike",
    requiredProperties = ["hikeId", "stages", "start", "end", "summary"],
)
data class Hike(
    @field:Schema(description = "Unique identifier of the hike")
    val hikeId: UUID,
    @field:Schema(description = "List of stage Ids of the hike")
    val stages: List<Long>,
    @field:Schema(implementation = StampingLocation::class)
    val start: StampingLocation,
    @field:Schema(implementation = StampingLocation::class)
    val end: StampingLocation,
    @field:Schema(description = "Summary of the hike")
    val summary: HikeSummary,
)
