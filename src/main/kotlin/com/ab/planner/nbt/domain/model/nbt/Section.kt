package com.ab.planner.nbt.domain.model.nbt

import kotlin.time.Duration

class Section(
    val id: Long,
    val stages: List<Stage>,
) {
    init {
        check(stages.isNotEmpty()) { "Section must have at least one stage" }
    }

    val startStampingLocation: StampingLocation get() = stages.first().start

    val endStampingLocation: StampingLocation get() = stages.last().end

    val length: Double get() = stages.sumOf { it.length }

    val elevationGain: Long get() = stages.sumOf { it.elevationGain }

    val elevationLoss: Long get() = stages.sumOf { it.elevationLoss }

    val timeFromStartToEnd: Duration get() = stages.fold(Duration.ZERO) { sum, stage -> sum + stage.timeFromStartToEnd }

    val timeFromEndToStart: Duration get() = stages.fold(Duration.ZERO) { sum, stage -> sum + stage.timeFromEndToStart }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Section) return false

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int = id.hashCode()
}
