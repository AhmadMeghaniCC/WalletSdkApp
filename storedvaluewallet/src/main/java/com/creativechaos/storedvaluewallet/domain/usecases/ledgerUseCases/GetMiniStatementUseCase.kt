package com.creativechaos.storedvaluewallet.domain.usecases.ledgerUseCases

import com.creativechaos.storedvaluewallet.Utils
import com.creativechaos.storedvaluewallet.data.model.LedgerRecord
import com.creativechaos.storedvaluewallet.domain.repo.LedgerRepository
import com.google.gson.Gson

class GetMiniStatementUseCase(private val ledgerRepository: LedgerRepository) {

    private val gson = Utils.getLedgerGsonObject()
    fun execute(
        noOfRecords: Int = 0,
        recordSize: Int,
        fileNumber: Int,
        accessKey: Int
    ): List<LedgerRecord> {
        return ledgerRepository.getMiniStatement(noOfRecords, fileNumber, accessKey, recordSize)
            .mapNotNull { json ->
                try {
                    gson.fromJson(json.replace("\u0000", ""), LedgerRecord::class.java)
                } catch (e: Exception) {
                    // Handle parsing errors here if needed
                    null
                }
            }
    }
}
