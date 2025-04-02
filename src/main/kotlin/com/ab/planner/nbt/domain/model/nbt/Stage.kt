package com.ab.planner.nbt.domain.model.nbt

import com.ab.planner.nbt.domain.model.plan.Direction
import com.ab.planner.nbt.domain.model.plan.Hike
import com.ab.planner.nbt.domain.model.plan.HikeSpecification
import kotlin.time.Duration

class Stage(
    val id: Long,
    val sectionId: Long,
    val start: StampingLocation,
    val end: StampingLocation,
    val length: Double,
    val elevationGain: Long,
    val elevationLoss: Long,
    val timeFromStartToEnd: Duration,
    val timeFromEndToStart: Duration,
) {
    // TODO #155: remove later
    fun createHikes(
        stages: List<Stage>,
        hikeSpecification: HikeSpecification,
    ): Set<Hike> {
        check(this in stages) { "Stage must be in the list of stages" }

        val plansFromStartToEnd: Set<Hike> =
            buildHikes(setOf(Hike(listOf(this), Direction.START_TO_END)), stages, hikeSpecification)
        val plansFromEndToStart: Set<Hike> =
            buildHikes(setOf(Hike(listOf(this), Direction.END_TO_START)), stages, hikeSpecification)

        return (
            plansFromStartToEnd.filter { hikeSpecification.canAccept(it) } +
                plansFromEndToStart.filter { hikeSpecification.canAccept(it) }
        ).toSet()
    }

    fun startStampingLocation(direction: Direction): StampingLocation =
        when (direction) {
            Direction.START_TO_END -> start
            Direction.END_TO_START -> end
        }

    fun endStampingLocation(direction: Direction): StampingLocation =
        when (direction) {
            Direction.START_TO_END -> end
            Direction.END_TO_START -> start
        }

    fun elevationGain(direction: Direction): Long =
        when (direction) {
            Direction.START_TO_END -> elevationGain
            Direction.END_TO_START -> elevationLoss
        }

    fun elevationLoss(direction: Direction): Long =
        when (direction) {
            Direction.START_TO_END -> elevationLoss
            Direction.END_TO_START -> elevationGain
        }

    fun time(direction: Direction): Duration =
        when (direction) {
            Direction.START_TO_END -> timeFromStartToEnd
            Direction.END_TO_START -> timeFromEndToStart
        }

    private fun buildHikes(
        hikesSoFar: Set<Hike>,
        stages: List<Stage>,
        hikeSpecification: HikeSpecification,
    ): Set<Hike> {
        val hikeToExtend = hikesSoFar.last()
        val direction = hikeToExtend.direction

        val stampingLocationToContinueFrom = endStampingLocation(direction)
        val nextStage: Stage = stampingLocationToContinueFrom.getNextStage(direction, stages) ?: return hikesSoFar

        val extendedHikes = hikeToExtend.copy(stages = hikeToExtend.stages + nextStage)
        val allHikes = hikesSoFar + extendedHikes

        if (hikeSpecification.canContinueWith(extendedHikes)) {
            return nextStage.buildHikes(allHikes, stages, hikeSpecification)
        }

        return allHikes
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Stage) return false

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int = id.hashCode()
}
