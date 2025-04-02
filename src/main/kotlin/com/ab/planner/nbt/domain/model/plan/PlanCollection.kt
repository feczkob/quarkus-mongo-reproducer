package com.ab.planner.nbt.domain.model.plan

import com.ab.planner.nbt.domain.mapAsync
import com.ab.planner.nbt.domain.model.travel.TransitSpecification
import java.util.UUID

class PlanCollection(
    val plans: List<Plan>,
    val id: UUID = UUID.randomUUID(),
) {
    suspend fun complyWith(planSpecification: PlanSpecification): PlanCollection {
        val eligiblePlans = plans.mapAsync { it.accept(planSpecification) }
            .filter { it.hasAnyTransitOption }

        if (eligiblePlans.isEmpty()) {
            throw NoSuchElementException("No plans found for the given specification")
        }

        return PlanCollection(plans = eligiblePlans, id = id)
    }

    suspend fun filterFor(transitSpecification: TransitSpecification): PlanCollection {
        val eligiblePlans =
            plans
                .mapAsync { it.accept(transitSpecification) }
                .filter { it.hasAnyTransitOption }

        if (eligiblePlans.isEmpty()) {
            throw NoSuchElementException("No plans found for the given specification")
        }

        return PlanCollection(plans = eligiblePlans, id = id)
    }
}
