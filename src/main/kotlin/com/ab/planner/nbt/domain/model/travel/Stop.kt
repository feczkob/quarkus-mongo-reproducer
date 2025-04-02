package com.ab.planner.nbt.domain.model.travel

private const val NAME_NOT_SET = ""
private const val NON_EXISTENT_ID = "NO_ID"

class Stop(
    val settlementId: String,
    val settlementName: String = NAME_NOT_SET,
    val stopId: String,
    val stopName: String = NAME_NOT_SET,
) {
    fun exists(): Boolean = settlementId != NON_EXISTENT_ID

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Stop) return false

        if (settlementId != other.settlementId) return false
        if (stopId != other.stopId) return false

        return true
    }

    override fun hashCode(): Int = settlementId.hashCode() + stopId.hashCode()

    override fun toString(): String = "Stop(id=$settlementId, settlementName='$settlementName', stopId=$stopId, stopName='$stopName')"
}

fun noStop(): Stop = Stop(settlementId = NON_EXISTENT_ID, stopId = NON_EXISTENT_ID)
