package com.creativechaos.storedvaluewallet.domain.usecases.tripInfoLogUseCases

import android.util.Log
import com.creativechaos.storedvaluewallet.Utils
import com.creativechaos.storedvaluewallet.data.model.TripInfoRecord
import com.creativechaos.storedvaluewallet.data.model.TripLogType
import com.creativechaos.storedvaluewallet.domain.repo.TripInfoRepository
import com.creativechaos.storedvaluewallet.presentation.StoreValueWallet
import com.google.gson.Gson

class AddTripInfoLogUseCase(private val tripInfoRepository: TripInfoRepository) {
    fun execute(
        tripInfoFileNumber: Int,
        tripId: String,
        waybillNo: Long,
        txnDateTime: Long,
        stationId: Long,
        amount: Int,
        merchantAccountId: Long,
        tripLogType: TripLogType,
        accessKey: Int
    ): Boolean {
        val data = Utils.getTripLogGsonObject().toJson(
            TripInfoRecord(
                tripId, waybillNo, txnDateTime, stationId, amount, merchantAccountId, tripLogType
            )

        )

        Log.i(StoreValueWallet.TAG, "AddTripInfoLogUseCase: $data")
        Log.i(StoreValueWallet.TAG, "AddTripInfoLogUseCase: ${data.toByteArray()}")
        Log.i(StoreValueWallet.TAG, "AddTripInfoLogUseCase: ${data.toByteArray().size}")

        return tripInfoRepository.addNewTripInfo(
            data.toByteArray(), tripInfoFileNumber = tripInfoFileNumber, accessKey
        )
    }
}