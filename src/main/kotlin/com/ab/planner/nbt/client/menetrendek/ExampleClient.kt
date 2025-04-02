package com.ab.planner.nbt.client.menetrendek


import com.example.model.BaseRequest
import com.example.model.RouteNativeData
import com.example.model.RouteRequest
import com.example.model.RouteRequestParams
import com.example.model.RouteResponse
import com.example.model.RouteResult
import com.example.model.Route
import com.example.model.RouteResultUsedParams
import com.example.model.StationRequest
import com.example.model.StationRequestParams
import com.example.model.StationResponse
import com.example.model.StationResult
import com.example.model.StationResultGeomEov
import io.quarkus.runtime.annotations.RegisterForReflection
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient

@RegisterRestClient(configKey = "example-api")
@RegisterForReflection(
    targets = [
        BaseRequest::class,
        Route::class,
        RouteNativeData::class,
        RouteRequest::class,
        RouteRequestParams::class,
        RouteResponse::class,
        RouteResult::class,
        RouteResultUsedParams::class,
        StationRequest::class,
        StationRequestParams::class,
        StationResponse::class,
        StationResult::class,
        StationResultGeomEov::class,
    ],
)
fun interface ExampleClient {
    @POST
    @Path("/index.php")
    suspend fun generalRequest(baseRequest: BaseRequest): Any
}
