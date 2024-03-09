package com.creativechaos.storedvaluewallet.domain.usecases.transactionLogUseCases

import android.util.Log
import com.creativechaos.storedvaluewallet.Utils
import com.creativechaos.storedvaluewallet.data.model.TransactionLogRecord
import com.creativechaos.storedvaluewallet.data.model.TransactionType
import com.creativechaos.storedvaluewallet.domain.repo.TransactionLogRepository
import com.creativechaos.storedvaluewallet.presentation.StoreValueWallet
import com.google.gson.Gson

class AddNewTransactionLogUseCase(private val transactionLogRepository: TransactionLogRepository) {
    fun execute(
        transactionLogFileNumber: Int,
        accessKey: Int,
        coRelationId: String,
        transactionType: TransactionType,
        merchantAccountId: Long,
        txnDateTime: Long,
        amount: Long
    ): Boolean {

        val transactionRecord = TransactionLogRecord(
            coRelationId, transactionType, merchantAccountId, txnDateTime, amount
        )

        val data = Utils.getTxnLogGsonObject().toJson(transactionRecord)

        Log.i(StoreValueWallet.TAG, "AddNewTransactionLogUseCase: $data")
        Log.i(StoreValueWallet.TAG, "AddNewTransactionLogUseCase: ${data.toByteArray()}")
        Log.i(StoreValueWallet.TAG, "AddNewTransactionLogUseCase: ${data.toByteArray().size}")

        return transactionLogRepository.newTransactionLog(
            data.toByteArray(),
            transactionLogFileNumber,
            accessKey
        )
    }
}