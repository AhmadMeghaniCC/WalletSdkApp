package com.creativechaos.storedvaluewallet.domain.usecases.salesLogsUseCases

import com.creativechaos.storedvaluewallet.Utils
import com.creativechaos.storedvaluewallet.data.model.SalesInfoRecord
import com.creativechaos.storedvaluewallet.domain.repo.SalesLogRepository

class GetSaleLogsUseCase(private val salesLogRepository: SalesLogRepository) {

    private val gson = Utils.getSalesLogGsonObject()
    fun execute(
        noOfRecords: Int = 0, recordSize: Int, salesLogFileNumber: Int, accessKey: Int
    ): List<SalesInfoRecord> {
        return salesLogRepository.getSalesLog(
            noOfRecords = noOfRecords,
            salesLogFileNumber = salesLogFileNumber,
            recordSize = recordSize,
            accessKey = accessKey
        )
            .mapNotNull { json ->
                try {
                    gson.fromJson(json.replace("\u0000", ""), SalesInfoRecord::class.java)
                } catch (e: Exception) {
                    // Handle parsing errors here if needed
                    null
                }
            }
    }
}