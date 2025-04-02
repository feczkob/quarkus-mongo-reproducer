package com.ab.planner.nbt.rest.validator

import jakarta.validation.ConstraintValidatorContext

fun ConstraintValidatorContext.setMessage(message: String) {
    disableDefaultConstraintViolation()
    buildConstraintViolationWithTemplate(message)
        .addConstraintViolation()
}
