package com.ab.planner.nbt.rest.nbt

import org.eclipse.microprofile.openapi.annotations.media.Schema

// TODO #155 remove later
@Schema(
    name = "StampingLocation",
    description = "Class representing a stamping location",
    requiredProperties = ["stampingLocationId", "name"],
)
data class StampingLocation(
    @field:Schema(description = "Id of the stamping location")
    val stampingLocationId: Long,
    @field:Schema(description = "Name of the stamping location")
    val name: String,
)
