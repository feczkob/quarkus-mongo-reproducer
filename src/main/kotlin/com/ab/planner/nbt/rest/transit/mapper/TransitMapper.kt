package com.ab.planner.nbt.rest.transit.mapper

import com.ab.planner.nbt.domain.model.travel.Transit
import com.ab.planner.nbt.domain.model.travel.TransitSpecification
import com.ab.planner.nbt.rest.transit.Settlement
import com.ab.planner.nbt.rest.transit.TransitSpecification as ApiTransitSpecification
import jakarta.enterprise.context.ApplicationScoped
import kotlin.time.Duration

@ApplicationScoped
class TransitMapper {
    // TODO #155: remove
    fun mapToDomain(transitSpecification: ApiTransitSpecification): TransitSpecification =
        TransitSpecification(
            maxNumberOfChanges = transitSpecification.maxNumberOfChanges,
            minWaitingTime = transitSpecification.minWaitingTime?.let { Duration.parseIsoString(it) },
            maxWaitingTime = transitSpecification.maxWaitingTime?.let { Duration.parseIsoString(it) },
            maxTravelTime = transitSpecification.maxTravelTime?.let { Duration.parseIsoString(it) },
        )

    // TODO #155: remove
    fun mapToApiTransitV1(transit: Transit): com.ab.planner.nbt.rest.transit.Transit {
        if (!transit.hasOption) {
            throw NoSuchElementException("No transit options found")
        }

        return com.ab.planner.nbt.rest.transit.Transit(
            travelDate = transit.date,
            from = Settlement(name = transit.from.settlementName, stop = transit.from.stopName),
            to = Settlement(name = transit.to.settlementName, stop = transit.to.stopName),
        )
    }
}
