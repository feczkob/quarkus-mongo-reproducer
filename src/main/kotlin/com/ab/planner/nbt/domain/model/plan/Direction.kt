package com.ab.planner.nbt.domain.model.plan

enum class Direction {
    START_TO_END,
    END_TO_START,
    ;

    operator fun unaryMinus(): Direction =
        when (this) {
            START_TO_END -> END_TO_START
            END_TO_START -> START_TO_END
        }
}
