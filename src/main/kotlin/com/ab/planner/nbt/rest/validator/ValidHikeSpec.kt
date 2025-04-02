package com.ab.planner.nbt.rest.validator

import jakarta.validation.Constraint
import kotlin.reflect.KClass

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
@Constraint(validatedBy = [HikeSpecValidator::class])
annotation class ValidHikeSpec(
    val message: String = "HikeSpec is not valid",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Any>> = [],
)
