package com.ab.planner.nbt.domain.model

import com.ab.planner.nbt.domain.handler.ExampleHandler
import com.ab.planner.nbt.domain.mapAsync
import com.ab.planner.nbt.domain.model.plan.Hike
import com.ab.planner.nbt.domain.model.plan.HikeCollection
import com.ab.planner.nbt.domain.model.plan.PlanBetweenStampingLocations
import com.ab.planner.nbt.domain.model.plan.PlanCollection
import com.ab.planner.nbt.domain.model.plan.PlanFromHome
import com.ab.planner.nbt.domain.model.travel.Stop
import com.ab.planner.nbt.domain.model.travel.Transit
import com.ab.planner.nbt.domain.plus
import java.time.LocalDate
import java.util.UUID
import kotlin.time.Duration

class PlanFactory(
    private val exampleHandler: ExampleHandler,
) {
    suspend fun buildPlanCollectionBetweenStampingLocations(
        hikeCollection: HikeCollection,
        travelDate: LocalDate,
    ): PlanCollection {
        val hikesEligibleForBeInPlan = hikeCollection.hikes.filter { it.isAccessible }
        val derivedPlans =
            hikesEligibleForBeInPlan
                .mapAsync { hike ->
                    PlanBetweenStampingLocations(
                        hike = hike,
                        transit =
                            exampleHandler.getTransit(
                                hike.end.stop,
                                hike.start.stop,
                                travelDate,
                            ),
                    )
                }.filter { it.hasAnyTransitOption }

        return PlanCollection(plans = derivedPlans)
    }

    suspend fun buildPlanCollectionFromHome(
        hikeCollection: HikeCollection,
        travelDate: LocalDate,
        home: Stop
    ): PlanCollection {
        val hikesEligibleForBeInPlan = hikeCollection.hikes.filter { it.isAccessible }
        val derivedPlans =
            hikesEligibleForBeInPlan
                .mapAsync { hike ->
                    val transitBeforeHike = exampleHandler.getTransit(
                        home,
                        hike.start.stop,
                        travelDate,
                    )
                    val transitAfterHike = exampleHandler.getTransit(
                        hike.end.stop,
                        home,
                        travelDate,
                    )
                    return@mapAsync constructPlanFromHome(hike, transitBeforeHike, transitAfterHike)
                }.filter { it.hasAnyTransitOption }

        return PlanCollection(plans = derivedPlans)
    }

    private fun constructPlanFromHome(hike: Hike, transitBeforeHike: Transit, transitAfterHike: Transit): PlanFromHome {
        val transitOptionPairs = workoutPossibleCombinations(transitBeforeHike, transitAfterHike, hike.totalHikeTime)
        return PlanFromHome(
            hike = hike,
            transitBeforeHike = transitBeforeHike.keepSelected(ids = transitOptionPairs.map { it.first }),
            transitAfterHike = transitAfterHike.keepSelected(ids = transitOptionPairs.map { it.second }),
            transitOptionPairs = transitOptionPairs,
        )
    }

    private fun workoutPossibleCombinations(transitBeforeHike: Transit, transitAfterHike: Transit, hikeTime: Duration): Set<Pair<UUID, UUID>> =
        transitBeforeHike.options.map { beforeHike ->
            transitAfterHike.options.filter { afterHike ->
                beforeHike.arrivalTime + hikeTime <= afterHike.departureTime
            }.map { afterHike ->
                beforeHike.id to afterHike.id
            }
        }.flatten().toSet()
}
