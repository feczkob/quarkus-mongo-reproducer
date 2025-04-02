package com.ab.planner.nbt.rest.plan

import com.ab.planner.nbt.rest.hike.Hike
import com.ab.planner.nbt.rest.transit.Transit
import org.eclipse.microprofile.openapi.annotations.media.Schema
import java.util.UUID

// TODO #155 remove later
@Schema(
    name = "Plan",
    description = "Class representing a complete plan. Note that the Transit goes in the opposite direction as the Hike.",
    requiredProperties = ["planId", "hike", "transit"],
)
data class Plan(
    @field:Schema(description = "Unique identifier of the plan")
    val planId: UUID,
    @field:Schema(implementation = Hike::class)
    val hike: Hike,
    @field:Schema(implementation = Transit::class)
    val transit: Transit,
)

