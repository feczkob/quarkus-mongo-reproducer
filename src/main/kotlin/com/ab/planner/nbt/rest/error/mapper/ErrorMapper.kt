package com.ab.planner.nbt.rest.error.mapper

import com.ab.planner.nbt.application.bean.RequestScopedBean
import com.ab.planner.nbt.domain.exception.ConversionException
import com.ab.planner.nbt.domain.exception.NotFoundException
import com.ab.planner.nbt.rest.error.ErrorCode
import com.ab.planner.nbt.rest.error.RestApiError
import com.fasterxml.jackson.databind.exc.MismatchedInputException
import jakarta.enterprise.context.ApplicationScoped
import jakarta.validation.ConstraintViolationException
import org.eclipse.microprofile.config.inject.ConfigProperty
import kotlin.reflect.KClass

private const val PROD_PROFILE = "prod"

@ApplicationScoped
class ErrorMapper(
    @ConfigProperty(name = "quarkus.profile")
    private val profile: String,
    private val requestScopedBean: RequestScopedBean,
) {
    private val exceptionToCode: Map<KClass<out Exception>, ErrorCode> =
        mapOf(
            ConversionException::class to ErrorCode.UNKNOWN_ERROR,
            IllegalArgumentException::class to ErrorCode.ILLEGAL_ARGUMENT,
            MismatchedInputException::class to ErrorCode.MISSING_REQUIRED_PARAM,
            NotFoundException::class to ErrorCode.MISSING_ENTITY,
            NoSuchElementException::class to ErrorCode.TOO_STRICT_SPECIFICATION,
            ConstraintViolationException::class to ErrorCode.ILLEGAL_ARGUMENT,
        )

    fun mapToApiError(exception: Exception): RestApiError =
        RestApiError(
            errorCode = mapToApiErrorCode(exception).code,
            message = exception.message ?: ErrorCode.UNKNOWN_ERROR.code,
            correlationId = requestScopedBean.correlationId,
            stackTrace =
                when (profile == PROD_PROFILE) {
                    true -> null
                    false -> exception.stackTraceToString()
                },
        )

    private fun mapToApiErrorCode(exception: Exception): ErrorCode {
        val matchedException = exceptionToCode.keys.firstOrNull { it.isInstance(exception) }
        return exceptionToCode[matchedException] ?: ErrorCode.UNKNOWN_ERROR
    }
}
