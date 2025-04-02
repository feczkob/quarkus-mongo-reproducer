package com.ab.planner.nbt.rest

import com.ab.planner.nbt.rest.plan.HikeSpecification
import com.ab.planner.nbt.rest.plan.PlanCollection
import com.ab.planner.nbt.rest.plan.PlanResourceV1
import io.quarkus.test.common.http.TestHTTPEndpoint
import io.quarkus.test.junit.QuarkusTest
import io.quarkus.test.junit.TestProfile
import io.restassured.RestAssured.given
import jakarta.ws.rs.core.MediaType
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments.arguments
import org.junit.jupiter.params.provider.MethodSource
import java.time.LocalDate

@QuarkusTest
@TestHTTPEndpoint(PlanResourceV1::class)
@TestProfile(DefaultTestProfile::class)
class PlanResourceV1Test {
    @ParameterizedTest
    @MethodSource("createPlansBetweenStampingLocations")
    fun `test create plans between stamping locations`(
        hikeSpecification: HikeSpecification,
        numOfExpectedResults: Int,
    ) {
        val response =
            given()
                .body(hikeSpecification)
                .header("Content-Type", MediaType.APPLICATION_JSON)
                .`when`()
                .post("/between-stamping-locations")
                .then()
                .statusCode(201)
                .and()
                .header("X-Total-Count", "$numOfExpectedResults")
                .and()
                .extract()
                .response()
                .`as`(PlanCollection::class.java)

        val planCollectionId = response.planCollectionId
        given()
            .`when`()
            .get("/collections/$planCollectionId")
            .then()
            .statusCode(200)

        val randomPlanId = response.plans.random().planId
        given()
            .`when`()
            .get("/$randomPlanId")
            .then()
            .statusCode(200)
    }

    companion object {
        @JvmStatic
        fun createPlansBetweenStampingLocations() =
            listOf(
                arguments(
                    HikeSpecification(
                        minHikeTime = "04:00",
                        maxHikeTime = "04:00",
                        preferredSections = "1,2",
                        travelDate = LocalDate.now(),
                    ),
                    2,
                ),
                arguments(
                    HikeSpecification(
                        minHikeTime = "06:00",
                        maxHikeTime = "08:00",
                        travelDate = LocalDate.now(),
                        preferredSections = "1-27",
                    ),
                    148,
                ),
                arguments(
                    HikeSpecification(
                        minHikeLength = 10.0,
                        maxHikeLength = 15.0,
                        preferredSections = "2",
                        travelDate = LocalDate.now(),
                    ),
                    6,
                ),
                arguments(
                    HikeSpecification(
                        minHikeLength = 15.0,
                        maxHikeLength = 20.0,
                        travelDate = LocalDate.now(),
                        preferredSections = "1-27",
                    ),
                    86,
                ),
            )
    }
}
