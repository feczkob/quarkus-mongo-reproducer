package com.ab.planner.nbt.domain.model.nbt

import com.ab.planner.nbt.domain.model.plan.Direction
import com.ab.planner.nbt.domain.model.travel.Stop
import com.ab.planner.nbt.domain.model.travel.noStop

class StampingLocation(
    val id: Long,
    val name: String,
    val stop: Stop = noStop(),
) {
    fun isAccessible(): Boolean = stop.exists()

    fun getNextStage(
        direction: Direction,
        stages: List<Stage>,
    ): Stage? =
        when (direction) {
            Direction.START_TO_END -> getNextStageFrom(stages)
            Direction.END_TO_START -> getPreviousStageFrom(stages)
        }

    private fun getNextStageFrom(stages: List<Stage>): Stage? = stages.find { it.start == this }

    private fun getPreviousStageFrom(stages: List<Stage>): Stage? = stages.find { it.end == this }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is StampingLocation) return false

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int = id.hashCode()

    override fun toString(): String = "StampingLocation(id=$id, name=$name)"
}
