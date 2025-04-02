package com.ab.planner.nbt.rest.plan.mapper

import com.ab.planner.nbt.domain.model.plan.PlanBetweenStampingLocations
import com.ab.planner.nbt.domain.model.plan.PlanCollection
import com.ab.planner.nbt.domain.model.plan.PlanSpecification
import com.ab.planner.nbt.rest.hike.mapper.HikeMapper
import com.ab.planner.nbt.rest.plan.HikeSpecification
import com.ab.planner.nbt.rest.plan.Plan
import com.ab.planner.nbt.rest.transit.mapper.TransitMapper
import jakarta.enterprise.context.ApplicationScoped

@ApplicationScoped
class PlanMapper(
    private val transitMapper: TransitMapper,
    private val hikeMapper: HikeMapper,
) {

    // TODO #155: remove
    fun mapToDomain(hikeSpecification: HikeSpecification): PlanSpecification =
        PlanSpecification(
            travelDate = hikeSpecification.travelDate,
            start = hikeSpecification.start,
            end = hikeSpecification.end,
        )

    // TODO #155: remove
    fun mapToApiV1(planCollection: PlanCollection): com.ab.planner.nbt.rest.plan.PlanCollection =
        com.ab.planner.nbt.rest.plan.PlanCollection(
            planCollectionId = planCollection.id,
            plans = planCollection.plans.map { mapToApiV1(it as PlanBetweenStampingLocations) },
        )

    // TODO #155: remove
    fun mapToApiV1(plan: PlanBetweenStampingLocations): Plan {
        check(plan.hasAnyTransitOption) { "The plan should have at least one transit option!" }

        return Plan(
            planId = plan.id,
            hike = hikeMapper.mapToApiV1(plan.hike),
            transit = transitMapper.mapToApiTransitV1(transit = plan.transit),
        )
    }
}
