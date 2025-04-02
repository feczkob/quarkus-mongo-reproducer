package com.ab.planner.nbt.domain

import com.ab.planner.nbt.domain.handler.ExampleHandler
import com.ab.planner.nbt.domain.handler.PlanHandler
import com.ab.planner.nbt.domain.model.plan.Plan
import com.ab.planner.nbt.domain.model.travel.Stop
import com.ab.planner.nbt.domain.model.travel.TransitSpecification
import java.util.UUID

class TransitManager(
    private val exampleHandler: ExampleHandler,
    private val planHandler: PlanHandler,
) {
    suspend fun getStops(stopName: String): List<Stop> = exampleHandler.getStops(stopName = stopName)

    suspend fun filterTransitOptions(
        planId: UUID,
        transitSpecification: TransitSpecification,
    ): Plan {
        val plan = planHandler.getPlan(planId).accept(transitSpecification)

        if (!plan.hasAnyTransitOption) {
            throw NoSuchElementException("No transit options found for the given specification")
        }

        return plan
    }

    suspend fun filterPlans(
        planCollectionId: UUID,
        transitSpecification: TransitSpecification,
    ): List<Plan> {
        val plans = planHandler.getPlanCollection(planCollectionId).filterFor(transitSpecification).plans

        if (plans.isEmpty()) {
            throw NoSuchElementException("No plans found for the given specification")
        }

        return plans
    }
}
