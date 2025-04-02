package com.ab.planner.nbt.domain.model.plan

import com.ab.planner.nbt.domain.model.travel.Transit
import com.ab.planner.nbt.domain.model.travel.TransitOption
import com.ab.planner.nbt.domain.model.travel.TransitSpecification
import java.util.UUID

sealed interface Plan {
    val id: UUID
    val hike: Hike
    val hasAnyTransitOption: Boolean

    fun accept(planSpecification: PlanSpecification): Plan
    fun accept(transitSpecification: TransitSpecification): Plan
}

data class PlanFromHome(
    override val id: UUID = UUID.randomUUID(),
    override val hike: Hike,
    val transitBeforeHike: Transit,
    val transitAfterHike: Transit,
    val transitOptionPairs: Set<Pair<UUID, UUID>> = setOf()
) : Plan {

    override val hasAnyTransitOption: Boolean get() = transitOptionPairs.isNotEmpty()

    override fun accept(planSpecification: PlanSpecification): PlanFromHome {
        return planSpecification.refinePlanFromHome(this)
    }

    override fun accept(transitSpecification: TransitSpecification): Plan {
        return transitSpecification.refinePlanFromHome(this)
    }
}

data class PlanBetweenStampingLocations(
    override val id: UUID = UUID.randomUUID(),
    override val hike: Hike,
    val transit: Transit,
) : Plan {

    override val hasAnyTransitOption: Boolean get() = transit.hasOption

    val transitOptions: List<TransitOption> get() = transit.options

    override fun accept(planSpecification: PlanSpecification): PlanBetweenStampingLocations {
        return planSpecification.refinePlanBetweenStampingLocations(this)
    }

    override fun accept(transitSpecification: TransitSpecification): PlanBetweenStampingLocations {
        return transitSpecification.refinePlanBetweenStampingLocations(this)
    }
}
