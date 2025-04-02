package com.ab.planner.nbt.rest.validator

import com.ab.planner.nbt.application.toDuration
import com.ab.planner.nbt.domain.minus
import com.ab.planner.nbt.rest.plan.HikeSpecification
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import kotlin.time.Duration.Companion.hours

// TODO #155 remove this class
class HikeSpecValidator : ConstraintValidator<ValidHikeSpec, HikeSpecification> {
    override fun isValid(
        hikeSpec: HikeSpecification,
        context: ConstraintValidatorContext,
    ): Boolean =
        checkType(hikeSpec, context) &&
                checkTimeHike(hikeSpec, context) &&
                checkDistanceHike(hikeSpec, context) &&
                checkTimeWindow(hikeSpec, context)

    private fun checkType(
        hikeSpecification: HikeSpecification,
        context: ConstraintValidatorContext,
    ): Boolean {
        if (hikeSpecification.minHikeTime != null &&
            hikeSpecification.maxHikeTime != null &&
            hikeSpecification.minHikeLength == null &&
            hikeSpecification.maxHikeLength == null
        ) {
            return true
        }
        if (hikeSpecification.minHikeLength != null &&
            hikeSpecification.maxHikeLength != null &&
            hikeSpecification.minHikeTime == null &&
            hikeSpecification.maxHikeTime == null
        ) {
            return true
        }
        context.setMessage("The hike should be based on either time or distance!")
        return false
    }

    private fun checkTimeHike(
        hikeSpecification: HikeSpecification,
        context: ConstraintValidatorContext,
    ): Boolean {
        val start = hikeSpecification.start
        val end = hikeSpecification.end
        val minHikeTime = hikeSpecification.minHikeTime?.toDuration()
        val maxHikeTime = hikeSpecification.maxHikeTime?.toDuration()

        if (minHikeTime != null && maxHikeTime != null) {
            if (maxHikeTime < minHikeTime) {
                context.setMessage("minHikeTime must be smaller than maxHikeTime")
                return false
            }
            if (maxHikeTime - minHikeTime > 2.hours) {
                context.setMessage("difference between minHikeTime and maxHikeTime must be reasonable (2 hours max.)")
                return false
            }
            if (start != null && end != null && maxHikeTime >= (end - start)) {
                context.setMessage("maxHikeTime $maxHikeTime must fit in to the [start, end] [$start, $end] time interval")
                return false
            }
        }

        return true
    }

    private fun checkDistanceHike(
        hikeSpecification: HikeSpecification,
        context: ConstraintValidatorContext,
    ): Boolean {
        val minHikeLength = hikeSpecification.minHikeLength
        val maxHikeLength = hikeSpecification.maxHikeLength

        if (minHikeLength != null && maxHikeLength != null) {
            if (maxHikeLength < minHikeLength) {
                context.setMessage("minHikeLength must not be greater than maxHikeLength")
                return false
            }
            if (maxHikeLength - minHikeLength > 5) {
                context.setMessage("difference between minHikeLength and maxHikeLength must be reasonable (5 kms max.)")
                return false
            }
        }

        return true
    }

    private fun checkTimeWindow(
        hikeSpecification: HikeSpecification,
        context: ConstraintValidatorContext,
    ): Boolean {
        val start = hikeSpecification.start
        val end = hikeSpecification.end

        if (start != null && end != null && !start.isBefore(end)) {
            context.setMessage("start must be before end")
            return false
        }

        return true
    }
}

