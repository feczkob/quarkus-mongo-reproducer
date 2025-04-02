package com.ab.planner.nbt.rest.plan

import org.eclipse.microprofile.openapi.annotations.media.Schema
import java.util.UUID

// TODO #155 remove later
@Schema(
    name = "PlanCollection",
    description = "Class representing a collection of plans",
    requiredProperties = ["planCollectionId", "plans"],
)
data class PlanCollection(
    @field:Schema(description = "Unique identifier of the plan collection")
    val planCollectionId: UUID,
    @field:Schema(description = "List of plans")
    val plans: List<Plan>,
)

