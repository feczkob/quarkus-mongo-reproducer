package com.ab.planner.nbt.rest.validator

import com.ab.planner.nbt.rest.transit.TransitSpecification
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import kotlin.time.Duration

class TransitSpecValidator : ConstraintValidator<ValidTransitSpec, TransitSpecification> {
    override fun isValid(
        transitSpec: TransitSpecification,
        context: ConstraintValidatorContext,
    ): Boolean {
        val minWaitingTime = transitSpec.minWaitingTime
        val maxWaitingTime = transitSpec.maxWaitingTime

        if (minWaitingTime == null || maxWaitingTime == null) {
            return true
        }

        if (Duration.parseIsoString(minWaitingTime) <= Duration.parseIsoString(maxWaitingTime)) {
            return true
        }

        context.setMessage("minWaitingTime must be smaller than or equal to maxWaitingTime")
        return false
    }
}

object TransitSpecValidatorV2 {
    fun validate(minWaitingTime: String?, maxWaitingTime: String?) {
        if (minWaitingTime == null || maxWaitingTime == null) {
            return
        }

        require(Duration.parseIsoString(minWaitingTime) <= Duration.parseIsoString(maxWaitingTime)) {
            "minWaitingTime must be smaller than or equal to maxWaitingTime"
        }
    }
}
