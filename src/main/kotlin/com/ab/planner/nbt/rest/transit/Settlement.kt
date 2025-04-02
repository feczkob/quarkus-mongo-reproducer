package com.ab.planner.nbt.rest.transit

import org.eclipse.microprofile.openapi.annotations.media.Schema

// TODO #155 remove later
@Schema(
    name = "Settlement",
    description = "Class representing a settlement with a stop",
    requiredProperties = ["name", "stop"],
)
data class Settlement(
    @field:Schema(description = "Name of the settlement")
    val name: String,
    @field:Schema(description = "Stop of the settlement")
    val stop: String,
)
