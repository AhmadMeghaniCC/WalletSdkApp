package com.creativechaos.storedvaluewallet.domain.usecases.tripInfoLogUseCases

import com.creativechaos.storedvaluewallet.Utils
import com.creativechaos.storedvaluewallet.data.model.TripInfoRecord
import com.creativechaos.storedvaluewallet.domain.repo.TripInfoRepository
import com.google.gson.Gson

class GetTripInfoLogsUseCase(private val tripInfoRepository: TripInfoRepository) {

    private val gson = Utils.getTripLogGsonObject()
    fun execute(
        tripInfoFileNumber: Int,
        noOfRecords: Int,
        recordSize: Int,
        accessKey: Int
    ): List<TripInfoRecord> {
        return tripInfoRepository.getTripInfoLog(
            noOfRecords = noOfRecords,
            recordSize = recordSize,
            tripInfoFileNumber = tripInfoFileNumber,
            accessKey = accessKey
        )
            .mapNotNull { json ->
                try {
                    gson.fromJson(json.replace("\u0000", ""), TripInfoRecord::class.java)
                } catch (e: Exception) {
                    // Handle parsing errors here if needed
                    null
                }
            }
    }
}