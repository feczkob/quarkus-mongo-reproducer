package com.ab.planner.nbt.client.nbt.mapper

import com.ab.planner.nbt.application.toDuration
import com.ab.planner.nbt.client.nbt.Stage
import com.ab.planner.nbt.domain.model.nbt.StampingLocation
import jakarta.enterprise.context.ApplicationScoped
import com.ab.planner.nbt.domain.model.nbt.Stage as DomainStage

@ApplicationScoped
class StageMapper {
    fun mapToDomain(
        stage: Stage,
        start: StampingLocation,
        end: StampingLocation,
    ): DomainStage =
        DomainStage(
            id = stage.stageId,
            sectionId = stage.sectionId,
            start = start,
            end = end,
            length = stage.length.replace(",", ".").toDouble(),
            elevationGain = stage.elevationGain,
            elevationLoss = stage.elevationLoss,
            timeFromEndToStart = stage.timeFromEndToStart.toDuration(),
            timeFromStartToEnd = stage.timeFromStartToEnd.toDuration(),
        )
}
