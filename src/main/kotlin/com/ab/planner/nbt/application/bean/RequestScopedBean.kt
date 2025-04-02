package com.ab.planner.nbt.application.bean

import io.vertx.core.http.HttpServerRequest
import jakarta.enterprise.context.RequestScoped
import jakarta.ws.rs.core.UriInfo
import java.util.UUID
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

@RequestScoped
class RequestScopedBean(
    val correlationId: String = UUID.randomUUID().toString(),
    private val startTime: Long = System.currentTimeMillis(),
) {
    val timeTaken: Duration
        get() = (System.currentTimeMillis() - startTime).milliseconds
}

interface WithHttpServerRequestAndUriInfo {
    val request: HttpServerRequest
    val uriInfo: UriInfo

    fun logMethodNameWithUriInfo() = "${request.method().name()} ${uriInfo.path}"
}
