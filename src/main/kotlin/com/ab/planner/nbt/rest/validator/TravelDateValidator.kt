package com.ab.planner.nbt.rest.validator

import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import java.time.LocalDate

class TravelDateValidator : ConstraintValidator<ValidTravelDate, LocalDate> {
    override fun isValid(
        travelDate: LocalDate,
        context: ConstraintValidatorContext,
    ): Boolean {
        val today = LocalDate.now()
        if (!travelDate.isBefore(today.plusWeeks(20).minusDays(1)) || travelDate.isBefore(today)) {
            context.setMessage("travelDate must be within 20 weeks from now")
            return false
        }

        return true
    }
}

object TravelDateValidatorV2 {
    fun validate(travelDate: LocalDate) {
        val today = LocalDate.now()
        require(travelDate.isBefore(today.plusWeeks(20).minusDays(1)) && !travelDate.isBefore(today)) { "travelDate must be within 20 weeks from now" }
    }
}
