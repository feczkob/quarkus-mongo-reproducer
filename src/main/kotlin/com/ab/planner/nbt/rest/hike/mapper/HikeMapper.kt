package com.ab.planner.nbt.rest.hike.mapper

import com.ab.planner.nbt.application.round
import com.ab.planner.nbt.domain.model.plan.Hike
import com.ab.planner.nbt.rest.nbt.mapper.StampingLocationMapper
import com.ab.planner.nbt.rest.plan.HikeSummary
import jakarta.enterprise.context.ApplicationScoped

@ApplicationScoped
class HikeMapper(
    private val stampingLocationMapper: StampingLocationMapper,
) {

    // TODO #155: remove
    fun mapToApiV1(hike: Hike): com.ab.planner.nbt.rest.hike.Hike =
        com.ab.planner.nbt.rest.hike.Hike(
            hikeId = hike.id,
            stages = hike.stages.map { it.id },
            start = stampingLocationMapper.mapToApiV1(hike.start),
            end = stampingLocationMapper.mapToApiV1(hike.end),
            summary =
                HikeSummary(
                    totalTime = hike.totalHikeTime.toIsoString(),
                    totalDistance = hike.totalLength.round(),
                    totalElevationGain = hike.elevationGain,
                    totalElevationLoss = hike.elevationLoss,
                ),
        )
}
