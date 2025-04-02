package com.ab.planner.nbt.client.nbt

class Stage(
    val sectionId: Long,
    val stageId: Long,
    val startId: Long,
    val endId: Long,
    val length: String,
    val elevationGain: Long,
    val elevationLoss: Long,
    val timeFromStartToEnd: String,
    val timeFromEndToStart: String,
)
