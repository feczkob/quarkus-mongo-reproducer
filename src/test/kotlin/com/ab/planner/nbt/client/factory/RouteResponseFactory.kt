package com.ab.planner.nbt.client.factory

import com.example.model.Route
import com.example.model.RouteNativeData
import com.example.model.RouteResponse
import com.example.model.RouteResult
import java.time.LocalDate

val simpleRouteResponse =
    RouteResponse(
        status = "success",
        results =
            RouteResult(
                dateGot = LocalDate.now().toString(),
                talalatok =
                    mapOf(
                        "1" to
                                Route(
                                    departureCity = "Budapest",
                                    departureStation = "Déli",
                                    departureLs = 12312,
                                    arrivalCity = "Pécs",
                                    arrivalStation = "Keleti",
                                    arrivalLs = 12313,
                                    indulasiIdo = "12:00",
                                    erkezesiIdo = "14:00",
                                    atszallasokSzama = 1,
                                    osszido = "02:00",
                                    riskyTransfer = false,
                                    totalFare = 400,
                                    nativeData =
                                        listOf(
                                            RouteNativeData(
                                                departureSettle = 12312,
                                                departureStation = 12312,
                                                fromSettle = "Budapest",
                                                depStationName = "Déli",
                                                departureTime = 720,
                                                arrivalSettle = 12313,
                                                arrivalStation = 12313,
                                                toSettle = "Pécs",
                                                arrStationName = "Keleti",
                                                arrivalTime = 840,
                                                transportMode = "vehicles.train",
                                                longName = "123",
                                            ),
                                        ),
                                ),
                    ),
            ),
        nativeResults = null,
    )

