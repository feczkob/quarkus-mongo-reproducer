package com.ab.planner.nbt.domain.model.plan

import com.ab.planner.nbt.domain.minus
import com.ab.planner.nbt.domain.model.travel.Transit
import com.ab.planner.nbt.domain.model.travel.TransitOption
import com.ab.planner.nbt.domain.plus
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import kotlin.time.Duration

class PlanSpecification(
    val travelDate: LocalDate,
    start: LocalTime?,
    end: LocalTime?,
) {

    init {
        check(start == null || end == null || start.isBefore(end)) {
            "start must be before end"
        }
    }

    private val start: LocalDateTime? = start?.let { LocalDateTime.of(travelDate, it) }

    private val end: LocalDateTime? = end?.let { LocalDateTime.of(travelDate, it) }

    fun refinePlanBetweenStampingLocations(plan: PlanBetweenStampingLocations): PlanBetweenStampingLocations {
        val eligibleOptions = plan.transit.options.filter {
            transitFollowedByHikeFits(it, plan.hike.totalHikeTime) || hikeFollowedByTransitFits(it, plan.hike.totalHikeTime)
        }

        return plan.copy(transit = Transit(plan.transit.date, eligibleOptions))
    }

    fun refinePlanFromHome(plan: PlanFromHome): PlanFromHome {
        val eligibleOptionPairs = plan.transitOptionPairs.filter { (before, after) ->
            val transitOptionBeforeHike = plan.transitBeforeHike[before]
            val transitOptionAfterHike = plan.transitAfterHike[after]

            transitsWithHikeInMiddleFit(transitOptionBeforeHike, transitOptionAfterHike, plan.hike.totalHikeTime)
        }.toSet()

        val transitOptionsBeforeHike = plan.transitBeforeHike.options.filter { it.id in eligibleOptionPairs.map { it.first } }
        val transitOptionsAfterHike = plan.transitAfterHike.options.filter { it.id in eligibleOptionPairs.map { it.second } }
        return plan.copy(
            transitBeforeHike = Transit(plan.transitBeforeHike.date, transitOptionsBeforeHike),
            transitAfterHike = Transit(plan.transitAfterHike.date, transitOptionsAfterHike),
            transitOptionPairs = eligibleOptionPairs
        )
    }

    private fun transitFollowedByHikeFits(
        transitOption: TransitOption,
        hikeTime: Duration,
    ): Boolean {
        if (start != null && transitOption.departureTime < start) return false
        if (end != null && transitOption.arrivalTime + hikeTime > end) return false
        return true
    }

    private fun hikeFollowedByTransitFits(
        transitOption: TransitOption,
        hikeTime: Duration,
    ): Boolean {
        if (start != null && transitOption.departureTime - hikeTime < start) return false
        if (end != null && transitOption.arrivalTime > end) return false
        return true
    }

    private fun transitsWithHikeInMiddleFit(
        transitOptionBeforeHike: TransitOption,
        transitOptionAfterHike: TransitOption,
        hikeTime: Duration,
    ): Boolean {
        if (start != null && transitOptionBeforeHike.departureTime < start) return false
        if (end != null && transitOptionAfterHike.arrivalTime > end) return false
        if (transitOptionBeforeHike.arrivalTime + hikeTime > transitOptionAfterHike.departureTime) return false
        return true
    }
}
