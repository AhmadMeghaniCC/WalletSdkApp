package com.creativechaos.storedvaluewallet.domain.usecases.salesLogsUseCases

import com.creativechaos.storedvaluewallet.Utils
import com.creativechaos.storedvaluewallet.data.model.SalesInfoRecord
import com.creativechaos.storedvaluewallet.domain.repo.SalesLogRepository

class GetLastSaleLogUseCase(private val salesLogRepository: SalesLogRepository) {

    private val gson = Utils.getSalesLogGsonObject()
    fun execute(accessKey: Int, recordSize: Int, salesLogFileNumber: Int): List<SalesInfoRecord> {
        return salesLogRepository.getSalesLog(
            noOfRecords = 1,
            salesLogFileNumber, recordSize,
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