package com.ab.planner.nbt.rest.transit

import com.ab.planner.nbt.rest.DURATION_REGEX
import com.ab.planner.nbt.rest.validator.ValidTransitSpec
import jakarta.validation.constraints.Pattern
import jakarta.ws.rs.QueryParam
import org.eclipse.microprofile.openapi.annotations.media.Schema

// TODO #155 remove later
@Schema(
    name = "TransitSpecification",
    description = "Class representing a transit specification",
)
@ValidTransitSpec
data class TransitSpecification(
    @field:QueryParam("maxNumberOfChanges")
    @field:Schema(description = "Maximum number of desired changes in public transport")
    var maxNumberOfChanges: Int? = null,
    @field:QueryParam("minWaitingTime")
    @field:Schema(
        description = "Minimum desired waiting time per change in ISO 8601 format [PThHmM]",
        pattern = DURATION_REGEX,
        example = "PT0H10M",
    )
    @field:Pattern(regexp = DURATION_REGEX)
    var minWaitingTime: String? = null,
    @field:QueryParam("maxWaitingTime")
    @field:Schema(
        description = "Maximum desired waiting time per change in ISO 8601 format [PThHmM]",
        pattern = DURATION_REGEX,
        example = "PT0H30M",
    )
    @field:Pattern(regexp = DURATION_REGEX)
    var maxWaitingTime: String? = null,
    @field:QueryParam("maxTravelTime")
    @field:Schema(
        description = "Maximum desired transit time in ISO 8601 format [PThHmM]",
        pattern = DURATION_REGEX,
        example = "PT1H30M",
    )
    @field:Pattern(regexp = DURATION_REGEX)
    var maxTravelTime: String? = null,
)
