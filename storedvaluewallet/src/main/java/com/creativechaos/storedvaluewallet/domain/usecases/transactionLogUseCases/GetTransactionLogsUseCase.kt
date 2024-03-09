package com.creativechaos.storedvaluewallet.domain.usecases.transactionLogUseCases

import com.creativechaos.storedvaluewallet.Utils
import com.creativechaos.storedvaluewallet.data.model.TransactionLogRecord
import com.creativechaos.storedvaluewallet.domain.repo.TransactionLogRepository
import com.google.gson.Gson

class GetTransactionLogsUseCase(private val transactionLogRepository: TransactionLogRepository) {

    private val gson = Utils.getTxnLogGsonObject()
    fun execute(
        noOfRecords: Int = 0,
        recordSize: Int,
        transactionLogFileNumber: Int,
        accessKey: Int
    ): List<TransactionLogRecord> {
        return transactionLogRepository.getTransactionLogs(
            noOfRecords,
            transactionLogFileNumber,
            accessKey = accessKey,
            recordSize = recordSize
        )
            .mapNotNull { json ->
                try {
                    gson.fromJson(json.replace("\u0000", ""), TransactionLogRecord::class.java)
                } catch (e: Exception) {
                    // Handle parsing errors here if needed
                    null
                }
            }
    }
}
