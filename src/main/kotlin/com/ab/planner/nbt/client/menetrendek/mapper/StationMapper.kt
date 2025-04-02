package com.ab.planner.nbt.client.menetrendek.mapper

import com.ab.planner.nbt.domain.model.travel.Stop
import com.example.model.StationRequest
import com.example.model.StationRequestParams
import com.example.model.StationResult
import jakarta.enterprise.context.ApplicationScoped

@ApplicationScoped
class StationMapper {
    fun mapToApi(stationName: String): StationRequest =
        StationRequest(
            type = "station",
            params = StationRequestParams(inputText = stationName),
        )

    fun mapToDomainStop(station: StationResult): Stop =
        Stop(
            settlementId = station.settlementId.toString(),
            settlementName = station.settlementName,
            stopId = station.lsId.toString(),
            stopName = station.lsname,
        )
}
