package com.creativechaos.storedvaluewallet.domain.usecases.salesLogsUseCases

import com.creativechaos.storedvaluewallet.Utils
import com.creativechaos.storedvaluewallet.data.model.SalesInfoRecord
import com.creativechaos.storedvaluewallet.data.model.TransactionType
import com.creativechaos.storedvaluewallet.domain.repo.SalesLogRepository
import com.google.gson.Gson

class AddNewSaleLogUseCase(private val salesLogRepository: SalesLogRepository) {
    fun execute(
        coRelationId: String,
        transactionType: TransactionType,
        merchantAccountId: Long,
        txnDateTime: Long,
        amount: Long,
        referenceId: String,
        salesLogFileNumber: Int,
        accessKey: Int
    ): Boolean {
        val data = Utils.getSalesLogGsonObject().toJson(
            SalesInfoRecord(
                coRelationId, transactionType, merchantAccountId, txnDateTime, amount, referenceId
            )
        )
        return salesLogRepository.newSale(data.toByteArray(), salesLogFileNumber, accessKey)
    }
}