package com.ab.planner.nbt.application.logger

import com.ab.planner.nbt.application.bean.RequestScopedBean
import com.ab.planner.nbt.application.bean.WithHttpServerRequestAndUriInfo
import io.quarkus.logging.Log
import io.quarkus.vertx.http.runtime.CurrentVertxRequest
import io.vertx.core.http.HttpServerRequest
import jakarta.enterprise.context.ApplicationScoped
import jakarta.ws.rs.client.ClientRequestContext
import jakarta.ws.rs.client.ClientRequestFilter
import jakarta.ws.rs.client.ClientResponseContext
import jakarta.ws.rs.client.ClientResponseFilter
import jakarta.ws.rs.container.ContainerRequestContext
import jakarta.ws.rs.container.ContainerRequestFilter
import jakarta.ws.rs.container.ContainerResponseContext
import jakarta.ws.rs.container.ContainerResponseFilter
import jakarta.ws.rs.core.UriInfo
import jakarta.ws.rs.ext.Provider
import org.jboss.logging.MDC

private const val CORRELATION_ID = "correlationId"
private const val REQUEST_ORIGIN = "requestOrigin"

@Provider
@ApplicationScoped
class RequestFilter(
    override val request: HttpServerRequest,
    override val uriInfo: UriInfo,
    val requestScopedBean: RequestScopedBean,
    private val currentVertxRequest: CurrentVertxRequest,
) : ContainerRequestFilter,
    WithHttpServerRequestAndUriInfo {
    override fun filter(requestContext: ContainerRequestContext) {
        MDC.put(CORRELATION_ID, requestScopedBean.correlationId)
        MDC.put(REQUEST_ORIGIN, request.remoteAddress().toString())
        Log.info("Request arrived: ${logMethodNameWithUriInfo()}")
    }
}

@Provider
@ApplicationScoped
class ResponseFilter(
    override val request: HttpServerRequest,
    override val uriInfo: UriInfo,
    val requestScopedBean: RequestScopedBean,
    private val currentVertxRequest: CurrentVertxRequest,
) : ContainerResponseFilter,
    WithHttpServerRequestAndUriInfo {
    override fun filter(
        requestContext: ContainerRequestContext,
        responseContext: ContainerResponseContext,
    ) {
        Log.info(
            "Request finished: ${logMethodNameWithUriInfo()} with status code " +
                "${responseContext.status}, time taken: ${requestScopedBean.timeTaken}",
        )
        MDC.remove(CORRELATION_ID)
        MDC.remove(REQUEST_ORIGIN)
    }
}

@Provider
@ApplicationScoped
class ClientRequestFilter : ClientRequestFilter {
    override fun filter(p0: ClientRequestContext?) = Log.debug("Calling client ${p0?.uri} with query object ${p0?.entity}")
}

@Provider
@ApplicationScoped
class ClientResponseFilter : ClientResponseFilter {
    override fun filter(
        p0: ClientRequestContext?,
        p1: ClientResponseContext?,
    ) = Log.debug("Received response from client ${p0?.uri} with status code ${p1?.status}")
}
