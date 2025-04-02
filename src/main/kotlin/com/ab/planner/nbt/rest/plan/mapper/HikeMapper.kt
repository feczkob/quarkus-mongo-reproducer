package com.ab.planner.nbt.rest.plan.mapper

import com.ab.planner.nbt.rest.plan.HikeSpecification as ApiHikeSpecification
import com.ab.planner.nbt.application.parseToSet
import com.ab.planner.nbt.application.toDuration
import com.ab.planner.nbt.domain.model.plan.DistanceHike
import com.ab.planner.nbt.domain.model.plan.HikeSpecification
import com.ab.planner.nbt.domain.model.plan.TimeHike
import io.quarkus.logging.Log
import jakarta.enterprise.context.ApplicationScoped

// TODO #155 remove later
@ApplicationScoped
class HikeMapper {

    fun mapToDomain(hikeSpecification: ApiHikeSpecification): HikeSpecification =
        when {
            hikeSpecification.minHikeTime != null -> constructTimeHike(hikeSpecification)
            hikeSpecification.minHikeLength != null -> constructDistanceHike(hikeSpecification)
            else -> throw IllegalArgumentException("The hike should be based on either time or distance!")
        }

    private fun constructTimeHike(hikeSpecification: ApiHikeSpecification): TimeHike {
        Log.info("Constructing a time-based plan query")
        return TimeHike(
            minHikeTime =
                hikeSpecification.minHikeTime?.toDuration()
                    ?: throw IllegalArgumentException("minHikeTime is required"),
            maxHikeTime =
                hikeSpecification.maxHikeTime?.toDuration()
                    ?: throw IllegalArgumentException("maxHikeTime is required"),
            preferredSections = hikeSpecification.preferredSections.parseToSet(),
        )
    }

    private fun constructDistanceHike(hikeSpecification: ApiHikeSpecification): DistanceHike {
        Log.info("Constructing a distance-based plan query")
        return DistanceHike(
            minHikeLength =
                hikeSpecification.minHikeLength
                    ?: throw IllegalArgumentException("minHikeLength is required"),
            maxHikeLength =
                hikeSpecification.maxHikeLength
                    ?: throw IllegalArgumentException("maxHikeLength is required"),
            preferredSections = hikeSpecification.preferredSections.parseToSet(),
        )
    }
}
