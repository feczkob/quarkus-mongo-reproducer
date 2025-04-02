package com.ab.planner.nbt.domain.model.travel

import com.ab.planner.nbt.domain.model.plan.PlanBetweenStampingLocations
import com.ab.planner.nbt.domain.model.plan.PlanFromHome
import kotlin.time.Duration

class TransitSpecification(
    private val maxNumberOfChanges: Int? = null,
    private val minWaitingTime: Duration? = null,
    private val maxWaitingTime: Duration? = null,
    private val maxTravelTime: Duration? = null,
) {

    fun refinePlanBetweenStampingLocations(plan: PlanBetweenStampingLocations): PlanBetweenStampingLocations {
        val refinedTransit = refineTransit(plan.transit)
        return plan.copy(transit = refinedTransit)
    }

    fun refinePlanFromHome(plan: PlanFromHome): PlanFromHome {
        val refinedTransitBeforeHike = refineTransit(plan.transitBeforeHike)
        val refinedTransitAfterHike = refineTransit(plan.transitAfterHike)

        val refinedTransitOptionPairs = plan.transitOptionPairs.filter { (before, after) ->
            refinedTransitBeforeHike.transitOptionIds.contains(before) && refinedTransitAfterHike.transitOptionIds.contains(after)
        }.toSet()
        return plan.copy(
            transitBeforeHike = refinedTransitBeforeHike,
            transitAfterHike = refinedTransitAfterHike,
            transitOptionPairs = refinedTransitOptionPairs
        )
    }

    private fun refineTransit(transit: Transit) = transit
        .filterByMaxChange(maxNumberOfChanges)
        .filterByWaitingTime(minWaitingTime to maxWaitingTime)
        .filterByTravelTime(maxTravelTime)
}
