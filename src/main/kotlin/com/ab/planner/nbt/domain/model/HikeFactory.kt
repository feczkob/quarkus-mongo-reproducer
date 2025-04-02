package com.ab.planner.nbt.domain.model

import com.ab.planner.nbt.domain.handler.NbtHandler
import com.ab.planner.nbt.domain.mapAsync
import com.ab.planner.nbt.domain.model.nbt.Stage
import com.ab.planner.nbt.domain.model.nbt.StampingLocation
import com.ab.planner.nbt.domain.model.plan.Direction
import com.ab.planner.nbt.domain.model.plan.Hike
import com.ab.planner.nbt.domain.model.plan.HikeCollection
import com.ab.planner.nbt.domain.model.plan.HikeSpecification
import com.ab.planner.nbt.domain.model.user.User
import com.ab.planner.nbt.domain.takeWhileInclusive

class HikeFactory(
    private val nbtHandler: NbtHandler,
) {
    suspend fun buildHikeCollection(
        user: User,
        hikeSpecification: HikeSpecification,
    ): HikeCollection {
        val nbt = nbtHandler.getNbt()
        nbt.validatePreferredSections(hikeSpecification)

        val hikes =
            nbt.stages
                .filter { stage -> !user.hasCompleted(stage) }
                .filter { stage -> hikeSpecification.canAccept(stage) }
                .let { it.mapAsync { stage -> createHikes(stage, it, hikeSpecification) } }
                .flatten()
                .filter { it.isAccessible }

        if (hikes.isEmpty()) {
            throw NoSuchElementException("No hikes found for the given specification!")
        }

        return HikeCollection(hikes)
    }

    suspend fun buildHikeCollection(
        stampingLocationAId: Long,
        stampingLocationBId: Long,
    ) : HikeCollection {
        val stampingLocationA = nbtHandler.getStampingLocation(stampingLocationAId)
        val stampingLocationB = nbtHandler.getStampingLocation(stampingLocationBId)

        val stages = stampingLocationA..stampingLocationB
        check(stages.isNotEmpty()) { "No stages found between the given stamping locations!" }

        val hike = Hike(stages, Direction.START_TO_END)
        require(hike.isAccessible) { "Hike is not accessible!" }

        return HikeCollection(listOf(hike, hike.reversed()))
    }

    private suspend operator fun StampingLocation.rangeTo(other: StampingLocation): List<Stage> {
        val stages = nbtHandler.getNbt().stages

        val (start, end) = if (this.id < other.id) this to other else other to this
        val hikeStages = stages.dropWhile { it.start != start }.takeWhileInclusive { it.end != end }

        return hikeStages
    }

    private fun createHikes(
        stage: Stage,
        stages: List<Stage>,
        hikeSpecification: HikeSpecification,
    ): Set<Hike> {
        val plansFromStartToEnd: Set<Hike> =
            buildHikes(stage, setOf(Hike(listOf(stage), Direction.START_TO_END)), stages, hikeSpecification)
        val plansFromEndToStart: Set<Hike> =
            buildHikes(stage, setOf(Hike(listOf(stage), Direction.END_TO_START)), stages, hikeSpecification)

        return (
            plansFromStartToEnd.filter { hikeSpecification.canAccept(it) } +
                plansFromEndToStart.filter { hikeSpecification.canAccept(it) }
        ).toSet()
    }

    private fun buildHikes(
        stage: Stage,
        hikesSoFar: Set<Hike>,
        stages: List<Stage>,
        hikeSpecification: HikeSpecification,
    ): Set<Hike> {
        val hikeToExtend = hikesSoFar.last()
        val direction = hikeToExtend.direction

        val stampingLocationToContinueFrom = stage.endStampingLocation(direction)
        val nextStage: Stage = stampingLocationToContinueFrom.getNextStage(direction, stages) ?: return hikesSoFar

        val extendedHike = hikeToExtend.copy(stages = hikeToExtend.stages + nextStage)
        val allHikes = hikesSoFar + extendedHike

        if (hikeSpecification.canContinueWith(extendedHike)) {
            return buildHikes(nextStage, allHikes, stages, hikeSpecification)
        }

        return allHikes
    }
}
