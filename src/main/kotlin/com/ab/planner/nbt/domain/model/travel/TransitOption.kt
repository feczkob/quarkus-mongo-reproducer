package com.ab.planner.nbt.domain.model.travel

import com.ab.planner.nbt.domain.minus
import java.time.LocalDateTime
import java.util.UUID
import kotlin.time.Duration

class TransitOption(
    val id: UUID = UUID.randomUUID(),
    val ticketPrice: Int?,
    val parts: List<TransitPart>,
) {
    fun isAllChangeTimeAcceptable(waitingTimeWindow: Pair<Duration?, Duration?>): Boolean =
        changeTimes.all {
            val minWaitingTime = waitingTimeWindow.first
            val maxWaitingTime = waitingTimeWindow.second

            if (minWaitingTime != null && it < minWaitingTime) return@all false
            if (maxWaitingTime != null && it > maxWaitingTime) return@all false

            return@all true
        }

    val from: Stop get() = parts.first().from

    val to: Stop get() = parts.last().to

    val departureTime: LocalDateTime get() = parts.first().departureTime

    val arrivalTime: LocalDateTime get() = parts.last().arrivalTime

    val travelTime: Duration get() = arrivalTime - departureTime

    val numberOfChanges: Int get() = parts.size - 1

    private val changeTimes: List<Duration> get() =
        parts.zipWithNext { current, next ->
            next.departureTime - current.arrivalTime
        }
}
