package com.creativechaos.storedvaluewallet.data.remote.apiDataClasses

import com.creativechaos.storedvaluewallet.data.model.TripLogType

data class TripSalesMetadata(
    val tripId: String,
    val waybillNo: Long,
    val stationId: Long,
    val tripLogType: TripLogType
)
