package com.ab.planner.nbt.domain.model.user

import com.ab.planner.nbt.domain.model.nbt.Stage

class User(
    private val completedStages: List<Long>,
) {
    fun hasCompleted(stage: Stage): Boolean = completedStages.contains(stage.id)
}
