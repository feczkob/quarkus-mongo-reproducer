package com.ab.planner.nbt.rest.validator

import jakarta.validation.Constraint
import kotlin.reflect.KClass

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FIELD)
@Constraint(validatedBy = [TravelDateValidator::class])
annotation class ValidTravelDate(
    val message: String = "Travel date is not valid",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Any>> = [],
)
