package com.ab.planner.nbt.rest.plan

import com.ab.planner.nbt.rest.validator.ValidTravelDate
import com.ab.planner.nbt.rest.validator.ValidHikeSpec
import jakarta.validation.constraints.FutureOrPresent
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Pattern
import org.eclipse.microprofile.openapi.annotations.media.Schema
import java.time.LocalDate
import java.time.LocalTime

// TODO #155 remove later
@Schema(
    name = "HikeSpecification",
    description =
        "Class representing a hike specification. " +
            "The difference between minHikeTime and maxHikeTime must be reasonable (max. 2 hours)." +
            "The difference between minHikeLength and maxHikeLength must be reasonable (max. 5 km)." +
            "The plan should be based on either time or distance: that is, " +
            "either minHikeTime and maxHikeTime, OR minHikeLength and maxHikeLength are set (but not both).",
    requiredProperties = ["travelDate", "preferredSections"],
)
@ValidHikeSpec
data class HikeSpecification(
    @field:Schema(
        description =
            "Date of the travel. Must not be null. Must not be in the past. " +
                "Must be before 20 weeks minus 1 day from now.",
        nullable = false,
    )
    @field:NotNull
    @field:FutureOrPresent
    @field:ValidTravelDate
    val travelDate: LocalDate,
    @field:Schema(
        pattern = """[0-9,-]+""",
        description =
            "Preferred sections of the hike. If null, then all sections are considered to be preferred. " +
                "If present, then should not be empty. \"-\" stands for the bulk register operation (example: \"1-4\").",
        example = "1,2",
    )
    @field:Pattern(regexp = "[0-9,-]+")
    @field:NotNull
    val preferredSections: String,
    @field:Schema(description = "Minimum time of the hike. Format: \"HH:mm\"", defaultValue = "04:00", pattern = "\\d{2}:\\d{2}")
    @Pattern(regexp = "\\d{2}:\\d{2}")
    val minHikeTime: String? = null,
    @field:Schema(description = "Maximum time of the hike. Format: \"HH:mm\"", defaultValue = "04:00", pattern = "\\d{2}:\\d{2}")
    @Pattern(regexp = "\\d{2}:\\d{2}")
    val maxHikeTime: String? = null,
    @field:Schema(description = "Minimum length of the hike [km]. Must be greater than 0. Default is 0.")
    @field:Min(0)
    val minHikeLength: Double? = null,
    @field:Schema(description = "Maximum length of the hike [km]. Must be greater than 0.")
    @field:Min(0)
    val maxHikeLength: Double? = null,
    @field:Schema(description = "Earliest desired start time (including the Hike and the Transit).", example = "07:00")
    val start: LocalTime? = null,
    @field:Schema(description = "Latest desired end time (including the Hike and the Transit).", example = "22:00")
    val end: LocalTime? = null,
)
