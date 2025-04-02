package com.ab.planner.nbt.rest.plan

import com.ab.planner.nbt.domain.PlanManager
import com.ab.planner.nbt.domain.TransitManager
import com.ab.planner.nbt.domain.model.plan.PlanBetweenStampingLocations
import com.ab.planner.nbt.domain.model.plan.PlanCollection as DomainPlanCollection
import com.ab.planner.nbt.rest.HEADER_TOTAL_COUNT
import com.ab.planner.nbt.rest.error.RestApiError
import com.ab.planner.nbt.rest.plan.mapper.HikeMapper
import com.ab.planner.nbt.rest.plan.mapper.PlanMapper
import com.ab.planner.nbt.rest.transit.TransitSpecification
import com.ab.planner.nbt.rest.transit.mapper.TransitMapper
import io.quarkus.logging.Log
import jakarta.validation.Valid
import jakarta.ws.rs.BeanParam
import jakarta.ws.rs.Consumes
import jakarta.ws.rs.GET
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import jakarta.ws.rs.PathParam
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType
import org.eclipse.microprofile.openapi.annotations.headers.Header
import org.eclipse.microprofile.openapi.annotations.media.Content
import org.eclipse.microprofile.openapi.annotations.media.Schema
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses
import java.net.URI
import java.util.UUID

@Path("/api/v1/plans")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Deprecated("Use PlanResourceV2 instead")
class PlanResourceV1(
    private val manager: PlanManager,
    private val transitManager: TransitManager,
    private val planMapper: PlanMapper,
    private val hikeMapper: HikeMapper,
    private val transitMapper: TransitMapper,
) {
    @APIResponses(
        value = [
            APIResponse(
                responseCode = "201",
                description = "Created",
                content = [
                    Content(
                        mediaType = MediaType.APPLICATION_JSON,
                        schema =
                            Schema(implementation = PlanCollection::class),
                    ),
                ],
                headers = [
                    Header(
                        name = HEADER_TOTAL_COUNT,
                        description = "Total count of plans",
                        schema = Schema(type = SchemaType.NUMBER),
                    ),
                ],
            ),
            APIResponse(
                responseCode = "400",
                description = "Bad request",
                content = [
                    Content(
                        mediaType = MediaType.APPLICATION_JSON,
                        schema = Schema(implementation = RestApiError::class),
                    ),
                ],
            ),
            APIResponse(
                responseCode = "500",
                description = "Internal server error",
                content = [
                    Content(
                        mediaType = MediaType.APPLICATION_JSON,
                        schema = Schema(implementation = RestApiError::class),
                    ),
                ],
            ),
        ],
    )
    @Schema(
        description = "Creates plans between stamping locations. The page should be 1.",
    )
    @POST
    @Path("/between-stamping-locations")
    suspend fun createPlansBetweenStampingLocationsV1(
        @Valid hikeSpecification: HikeSpecification,
    ): Response {

        val hikeSpec = hikeMapper.mapToDomain(hikeSpecification)
        val planSpec = planMapper.mapToDomain(hikeSpecification)

        val planCollection = manager.makePlansBetweenStampingLocations(hikeSpec, planSpec)

        Log.info("Number of plans: ${planCollection.plans.size}")

        val preparedCollection = DomainPlanCollection(planCollection.plans, planCollection.id)

        val result = planMapper.mapToApiV1(preparedCollection)

        return Response
            .created(URI("/api/v1/plans/collections/${result.planCollectionId}"))
            .entity(result)
            .header(HEADER_TOTAL_COUNT, planCollection.plans.size)
            .build()
    }

    @APIResponses(
        value = [
            APIResponse(
                responseCode = "200",
                description = "OK",
                content = [
                    Content(
                        mediaType = MediaType.APPLICATION_JSON,
                        schema =
                            Schema(
                                type = SchemaType.ARRAY,
                                implementation = Plan::class,
                            ),
                    ),
                ],
                headers = [
                    Header(
                        name = HEADER_TOTAL_COUNT,
                        description = "Total count of plans",
                        schema = Schema(type = SchemaType.NUMBER),
                    ),
                ],
            ),
            APIResponse(
                responseCode = "400",
                description = "Bad request",
                content = [
                    Content(
                        mediaType = MediaType.APPLICATION_JSON,
                        schema = Schema(implementation = RestApiError::class),
                    ),
                ],
            ),
            APIResponse(
                responseCode = "404",
                description = "Not found",
                content = [
                    Content(
                        mediaType = MediaType.APPLICATION_JSON,
                        schema = Schema(implementation = RestApiError::class),
                    ),
                ],
            ),
            APIResponse(
                responseCode = "500",
                description = "Internal server error",
                content = [
                    Content(
                        mediaType = MediaType.APPLICATION_JSON,
                        schema = Schema(implementation = RestApiError::class),
                    ),
                ],
            ),
        ],
    )
    @GET
    @Path("/collections/{planCollectionId}")
    suspend fun getPlansV1(
        @PathParam("planCollectionId") planCollectionId: UUID,
        @BeanParam @Valid transitSpecification: TransitSpecification,
    ): Response {
        val transitSpec = transitMapper.mapToDomain(transitSpecification = transitSpecification)

        val filteredPlans =
            transitManager
                .filterPlans(
                    planCollectionId = planCollectionId,
                    transitSpecification = transitSpec,
                )

        Log.info("Number of plans: ${filteredPlans.size}")

        val result =
            filteredPlans.map { planMapper.mapToApiV1(it as PlanBetweenStampingLocations) }

        return Response
            .ok(result)
            .header(HEADER_TOTAL_COUNT, filteredPlans.size)
            .build()
    }

    @APIResponses(
        value = [
            APIResponse(
                responseCode = "200",
                description = "OK",
                content = [
                    Content(
                        mediaType = MediaType.APPLICATION_JSON,
                        schema = Schema(implementation = Plan::class),
                    ),
                ],
            ),
            APIResponse(
                responseCode = "400",
                description = "Bad request",
                content = [
                    Content(
                        mediaType = MediaType.APPLICATION_JSON,
                        schema = Schema(implementation = RestApiError::class),
                    ),
                ],
            ),
            APIResponse(
                responseCode = "404",
                description = "Not found",
                content = [
                    Content(
                        mediaType = MediaType.APPLICATION_JSON,
                        schema = Schema(implementation = RestApiError::class),
                    ),
                ],
            ),
            APIResponse(
                responseCode = "500",
                description = "Internal server error",
                content = [
                    Content(
                        mediaType = MediaType.APPLICATION_JSON,
                        schema = Schema(implementation = RestApiError::class),
                    ),
                ],
            ),
        ],
    )
    @GET
    @Path("/{planId}")
    suspend fun getPlanV1(
        @PathParam("planId") planId: UUID,
    ): Response {
        val result = manager.getPlan(planId) as PlanBetweenStampingLocations
        return Response.ok(planMapper.mapToApiV1(result)).build()
    }
}
