package com.ab.planner.nbt.client.nbt.mapper

import com.ab.planner.nbt.client.nbt.StampingLocation
import com.ab.planner.nbt.domain.model.nbt.StampingLocation as DomainStampingLocation
import com.ab.planner.nbt.domain.model.travel.Stop
import com.ab.planner.nbt.domain.model.travel.noStop
import jakarta.enterprise.context.ApplicationScoped

@ApplicationScoped
class StampingLocationMapper {
    fun mapToDomain(stampingLocation: StampingLocation): DomainStampingLocation =
        DomainStampingLocation(
            id = stampingLocation.stampingLocationId,
            name = stampingLocation.stampingLocation,
            stop = mapToDomainStop(stampingLocation),
        )

    private fun mapToDomainStop(stampingLocation: StampingLocation): Stop =
        stampingLocation.settlementId?.let {
            Stop(
                settlementId = it.toString(),
                stopId = stampingLocation.stopId?.toString() ?: error("stopId must not be null"),
            )
        } ?: noStop()
}
