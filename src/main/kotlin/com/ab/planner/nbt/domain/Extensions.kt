package com.ab.planner.nbt.domain

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.temporal.ChronoUnit
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

operator fun LocalTime.minus(other: LocalTime): Duration = (this.toSecondOfDay() - other.toSecondOfDay()).seconds

operator fun LocalTime.minus(duration: Duration): Duration = this.toSecondOfDay().seconds - duration

operator fun LocalDateTime.plus(duration: Duration): LocalDateTime = this.plusMinutes(duration.inWholeMinutes)

operator fun LocalDateTime.minus(duration: Duration): LocalDateTime = this + (-duration)

operator fun LocalDateTime.minus(start: LocalDateTime): Duration = ChronoUnit.MINUTES.between(start, this).minutes

suspend fun <T, R> Collection<T>.mapAsync(transform: suspend (T) -> R): List<R> =
    coroutineScope {
        map { async { transform(it) } }.awaitAll()
    }

fun <T> List<T>.takeWhileInclusive(pred: (T) -> Boolean): List<T> {
    var shouldContinue = true
    return takeWhile {
        val result = shouldContinue
        shouldContinue = pred(it)
        result
    }
}
