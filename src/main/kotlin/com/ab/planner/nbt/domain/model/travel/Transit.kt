package com.ab.planner.nbt.domain.model.travel

import java.time.LocalDate
import java.util.UUID
import kotlin.time.Duration

data class Transit(
    val date: LocalDate,
    val options: List<TransitOption>,
) {
    fun filterByMaxChange(maxNumberOfChanges: Int?): Transit {
        if (maxNumberOfChanges == null) return this

        val eligibleOptions = options.filter { it.numberOfChanges <= maxNumberOfChanges }
        return Transit(date, eligibleOptions)
    }

    fun filterByWaitingTime(waitingTimeWindow: Pair<Duration?, Duration?>): Transit {
        val eligibleOptions = options.filter { it.isAllChangeTimeAcceptable(waitingTimeWindow) }
        return Transit(date, eligibleOptions)
    }

    fun filterByTravelTime(maxTravelTime: Duration?): Transit {
        if (maxTravelTime == null) return this

        val eligibleOptions = options.filter { it.travelTime <= maxTravelTime }
        return Transit(date, eligibleOptions)
    }

    fun keepSelected(ids: List<UUID>) : Transit {
        val selectedOptions = options.filter { it.id in ids }
        return copy(options = selectedOptions)
    }

    operator fun get(id: UUID) = options.find { it.id == id } ?: error("No transit option with id $id")

    val hasOption: Boolean get() = options.isNotEmpty()

    val from: Stop get() = options.first().from

    val to: Stop get() = options.first().to

    val transitOptionIds: List<UUID> get() = options.map { it.id }

    companion object {
        fun empty(date: LocalDate): Transit = Transit(date = date, options = emptyList())
    }
}
