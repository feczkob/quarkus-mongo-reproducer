package com.ab.planner.nbt.rest.error

import com.ab.planner.nbt.domain.exception.ConversionException
import com.ab.planner.nbt.domain.exception.NotFoundException
import com.ab.planner.nbt.rest.error.mapper.ErrorMapper
import com.fasterxml.jackson.databind.exc.MismatchedInputException
import io.quarkus.hibernate.validator.runtime.jaxrs.ResteasyReactiveViolationException
import io.quarkus.logging.Log
import jakarta.enterprise.context.ApplicationScoped
import jakarta.validation.ConstraintViolationException
import jakarta.ws.rs.WebApplicationException
import jakarta.ws.rs.core.Response
import jakarta.ws.rs.ext.ExceptionMapper
import jakarta.ws.rs.ext.Provider
import org.jboss.logging.MDC

@Provider
@ApplicationScoped
class ConversionExceptionHandler(
    private val errorMapper: ErrorMapper,
) : ExceptionMapper<ConversionException> {
    override fun toResponse(exception: ConversionException): Response {
        logError(exception)

        return Response
            .status(Response.Status.INTERNAL_SERVER_ERROR)
            .entity(errorMapper.mapToApiError(exception))
            .build()
    }
}

@Provider
@ApplicationScoped
class IllegalArgumentExceptionHandler(
    private val errorMapper: ErrorMapper,
) : ExceptionMapper<IllegalArgumentException> {
    override fun toResponse(exception: IllegalArgumentException): Response {
        logError(exception)

        return Response
            .status(Response.Status.BAD_REQUEST)
            .entity(errorMapper.mapToApiError(exception))
            .build()
    }
}

@Provider
@ApplicationScoped
class MismatchedInputExceptionHandler(
    private val errorMapper: ErrorMapper,
) : ExceptionMapper<MismatchedInputException> {
    override fun toResponse(exception: MismatchedInputException): Response {
        logError(exception)

        return Response
            .status(Response.Status.BAD_REQUEST)
            .entity(errorMapper.mapToApiError(exception))
            .build()
    }
}

@Provider
@ApplicationScoped
class NotFoundExceptionHandler(
    private val errorMapper: ErrorMapper,
) : ExceptionMapper<NotFoundException> {
    override fun toResponse(exception: NotFoundException): Response {
        logError(exception)

        return Response
            .status(Response.Status.NOT_FOUND)
            .entity(errorMapper.mapToApiError(exception))
            .build()
    }
}

private const val HTTP_UNPROCESSABLE_CONTENT = 422
@Provider
@ApplicationScoped
class NoSuchElementExceptionHandler(
    private val errorMapper: ErrorMapper,
) : ExceptionMapper<NoSuchElementException> {
    override fun toResponse(exception: NoSuchElementException): Response {
        logError(exception)

        return Response
            .status(HTTP_UNPROCESSABLE_CONTENT)
            .entity(errorMapper.mapToApiError(exception))
            .build()
    }
}

@Provider
@ApplicationScoped
class WebApplicationExceptionExceptionHandler(
    private val errorMapper: ErrorMapper,
) : ExceptionMapper<WebApplicationException> {
    override fun toResponse(exception: WebApplicationException): Response {
        logError(exception)

        return Response
            .status(exception.response.status)
            .entity(errorMapper.mapToApiError(exception))
            .build()
    }
}

@Provider
@ApplicationScoped
class ConstraintViolationExceptionHandler(
    private val errorMapper: ErrorMapper,
) : ExceptionMapper<ConstraintViolationException> {
    override fun toResponse(exception: ConstraintViolationException): Response {
        logError(exception)

        return Response
            .status(Response.Status.BAD_REQUEST)
            .entity(errorMapper.mapToApiError(exception))
            .build()
    }
}

@Provider
@ApplicationScoped
class ResteasyReactiveViolationExceptionExceptionHandler(
    private val errorMapper: ErrorMapper,
) : ExceptionMapper<ResteasyReactiveViolationException> {
    override fun toResponse(exception: ResteasyReactiveViolationException): Response {
        logError(exception)

        return Response
            .status(Response.Status.BAD_REQUEST)
            .entity(errorMapper.mapToApiError(exception))
            .build()
    }
}

@Provider
@ApplicationScoped
class BasicExceptionHandler(
    private val errorMapper: ErrorMapper,
) : ExceptionMapper<Exception> {
    override fun toResponse(exception: Exception): Response {
        logError(exception)

        return Response
            .status(Response.Status.INTERNAL_SERVER_ERROR)
            .entity(errorMapper.mapToApiError(exception))
            .build()
    }
}

private fun logError(exception: Exception) {
    MDC.put("StackTrace", exception.stackTraceToString())
    Log.error(exception.stackTraceToString())
    MDC.remove("StackTrace")
}
