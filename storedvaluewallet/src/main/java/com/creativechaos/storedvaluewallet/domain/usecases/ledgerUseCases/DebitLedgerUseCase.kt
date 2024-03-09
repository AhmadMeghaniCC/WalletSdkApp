package com.creativechaos.storedvaluewallet.domain.usecases.ledgerUseCases

import android.util.Log
import com.creativechaos.storedvaluewallet.Utils
import com.creativechaos.storedvaluewallet.data.model.LedgerRecord
import com.creativechaos.storedvaluewallet.data.model.LedgerType
import com.creativechaos.storedvaluewallet.domain.repo.LedgerRepository
import com.creativechaos.storedvaluewallet.presentation.StoreValueWallet
import com.google.gson.Gson

class DebitLedgerUseCase(private val ledgerRepository: LedgerRepository) {
    fun execute(amount: Int, dateTime: Long, versionNo: Long, fileNumber: Int, accessKey: Int): Boolean {
        val data = Utils.getLedgerGsonObject().toJson(
            LedgerRecord(
                amount.toLong(),
                dateTime,
                LedgerType.Debit,
                versionNo
            )
        )


        Log.i(StoreValueWallet.TAG, "DebitLedgerUseCase: $data")
        Log.i(StoreValueWallet.TAG, "DebitLedgerUseCase: ${data.toByteArray()}")
        Log.i(StoreValueWallet.TAG, "DebitLedgerUseCase: ${data.toByteArray().size}")
        return ledgerRepository.addLedgerEntry(data.toByteArray(), fileNumber, accessKey)
    }

}