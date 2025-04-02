package com.ab.planner.nbt.rest.error

import org.eclipse.microprofile.openapi.annotations.media.Schema

@Schema(
    name = "RestApiError",
    description = "Class representing a REST API error",
    requiredProperties = ["errorCode", "message", "correlationId"],
)
data class RestApiError(
    @field:Schema(description = "Error code")
    val errorCode: String = ErrorCode.UNKNOWN_ERROR.code,
    @field:Schema(description = "Error message")
    val message: String = ErrorCode.UNKNOWN_ERROR.code,
    @field:Schema(description = "correlationId of the flow in which the error occurred")
    val correlationId: String,
    @field:Schema(description = "Stack trace of the error")
    val stackTrace: String? = null,
)

enum class ErrorCode(
    val code: String,
) {
    UNKNOWN_ERROR("error.unknown"),
    MISSING_ENTITY("error.missing.entity"),
    ILLEGAL_ARGUMENT("error.illegal.argument"),
    MISSING_REQUIRED_PARAM("error.missing.required.param"),
    TOO_STRICT_SPECIFICATION("error.too.strict.specification"),
}
