package com.ab.planner.nbt.application

import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

// TODO #155 move to StageMapper
fun String.toDuration(): Duration {
    val parts = split(":")
    return parts[0].toInt().hours + parts[1].toInt().minutes
}

fun Double.round(decimals: Int = 2): Double = "%.${decimals}f".format(this).toDouble()

private const val SEPARATOR = ","
private const val BULK_REGISTER_SYMBOL = "-"

// TODO #155 remove later
fun String?.parseToSet(): Set<Long> {
    if (this == null) return setOf()

    require(this.isNotEmpty()) { "Must not be empty" }

    require(matches(Regex("""[0-9$SEPARATOR$BULK_REGISTER_SYMBOL]+"""))) {
        throw IllegalArgumentException("Must contain only numbers, commas and dashes")
    }

    return split(SEPARATOR)
        .flatMap {
            return@flatMap when {
                !it.contains(BULK_REGISTER_SYMBOL) -> listOf(it.toLong())
                else -> {
                    val (start, end) = it.split(BULK_REGISTER_SYMBOL).map { it.toLong() }
                    require(start < end) { "Start must be smaller than end: $start >= $end" }
                    (start..end).toList()
                }
            }
        }.toSet()
}
