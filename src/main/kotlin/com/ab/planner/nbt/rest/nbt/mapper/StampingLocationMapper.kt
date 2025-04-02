package com.ab.planner.nbt.rest.nbt.mapper

import com.ab.planner.nbt.domain.model.nbt.StampingLocation
import jakarta.enterprise.context.ApplicationScoped

@ApplicationScoped
class StampingLocationMapper {
    // TODO #155: remove
    fun mapToApiV1(stampingLocation: StampingLocation): com.ab.planner.nbt.rest.nbt.StampingLocation =
        com.ab.planner.nbt.rest.nbt.StampingLocation(
            stampingLocationId = stampingLocation.id,
            name = stampingLocation.name,
        )
}
