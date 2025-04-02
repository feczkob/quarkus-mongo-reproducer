package com.ab.planner.nbt.domain.model.plan

import com.ab.planner.nbt.domain.model.nbt.Stage
import com.ab.planner.nbt.domain.model.nbt.StampingLocation
import java.util.UUID
import kotlin.time.Duration

data class Hike(
    val stages: List<Stage>,
    val direction: Direction,
    val id: UUID = UUID.randomUUID(),
) {
    val totalHikeTime: Duration get() = stages.fold(Duration.ZERO) { acc, stage -> acc + stage.time(direction) }

    val totalLength: Double get() = stages.sumOf { it.length }

    val start: StampingLocation get() = stages.first().startStampingLocation(direction)

    val end: StampingLocation get() = stages.last().endStampingLocation(direction)

    val elevationGain: Long get() = stages.sumOf { it.elevationGain(direction) }

    val elevationLoss: Long get() = stages.sumOf { it.elevationLoss(direction) }

    val isAccessible: Boolean get() = start.isAccessible() && end.isAccessible()

    fun reversed(): Hike = Hike(stages = stages.reversed(), direction = -direction)
}
